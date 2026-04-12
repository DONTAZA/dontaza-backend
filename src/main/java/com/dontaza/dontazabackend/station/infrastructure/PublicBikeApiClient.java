package com.dontaza.dontazabackend.station.infrastructure;

import com.dontaza.dontazabackend.station.domain.Station;
import com.dontaza.dontazabackend.station.domain.StationRepository;
import com.dontaza.dontazabackend.station.infrastructure.dto.PublicBikeApiResponse;
import com.dontaza.dontazabackend.station.infrastructure.dto.PublicBikeApiResponse.Item;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Slf4j
@Component
public class PublicBikeApiClient {

    private static final int PAGE_SIZE = 1000;

    private final PublicBikeApiProperties properties;
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final StationRepository stationRepository;

    public PublicBikeApiClient(PublicBikeApiProperties properties, ObjectMapper objectMapper,
                               StationRepository stationRepository) {
        this.properties = properties;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(2))
                .build();
        this.objectMapper = objectMapper;
        this.stationRepository = stationRepository;
    }

    @Scheduled(initialDelay = 0, fixedDelay = 300_000)
    @Transactional
    public void syncAllStations() {
        try {
            List<Station> stations = loadAllStationsFromApi();
            stations.forEach(this::upsertStation);
            log.info("Station sync completed: {} stations", stations.size());
        } catch (Exception e) {
            log.warn("Station sync failed: {}", e.getMessage());
        }
    }

    private void upsertStation(Station station) {
        stationRepository.findById(station.getId())
                .ifPresentOrElse(
                        existing -> existing.updateFrom(station),
                        () -> stationRepository.save(station)
                );
    }

    private List<Station> loadAllStationsFromApi() {
        return properties.regionCodes().stream()
                .flatMap(code -> fetchStationsByRegion(code).stream())
                .toList();
    }

    private List<Station> fetchStationsByRegion(String regionCode) {
        List<Station> result = new ArrayList<>();
        try {
            fetchAllPages(regionCode, result);
            log.info("Fetched {} stations for region {}", result.size(), regionCode);
        } catch (Exception e) {
            log.warn("Failed to fetch stations for region {}: {}", regionCode, e.getMessage());
        }
        return result;
    }

    private void fetchAllPages(String regionCode, List<Station> result) throws Exception {
        int pageNo = 1;
        while (true) {
            PublicBikeApiResponse response = requestPage(regionCode, pageNo);
            List<Station> stations = toStations(response);
            result.addAll(stations);
            int totalCount = Integer.parseInt(response.body().totalCount());
            if (result.size() >= totalCount || stations.isEmpty()) {
                break;
            }
            pageNo++;
        }
    }

    private static final int MAX_RETRIES = 3;

    private PublicBikeApiResponse requestPage(String regionCode, int pageNo) throws Exception {
        HttpRequest request = buildHttpRequest(regionCode, pageNo);
        return sendWithRetry(request);
    }

    private HttpRequest buildHttpRequest(String regionCode, int pageNo) throws Exception {
        String query = "serviceKey=" + properties.serviceKey()
                + "&pageNo=" + pageNo
                + "&numOfRows=" + PAGE_SIZE
                + "&type=json&lcgvmnInstCd=" + regionCode;
        URI uri = new URI("https", "apis.data.go.kr",
                "/B551982/pbdo_v2/inf_101_00010002_v2", query, null);
        return HttpRequest.newBuilder()
                .uri(uri).timeout(Duration.ofSeconds(5)).GET().build();
    }

    private PublicBikeApiResponse sendWithRetry(HttpRequest request) throws Exception {
        Exception lastException = null;
        for (int attempt = 0; attempt < MAX_RETRIES; attempt++) {
            try {
                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                return objectMapper.readValue(response.body(), PublicBikeApiResponse.class);
            } catch (Exception e) {
                lastException = e;
                sleepWithBackoff(attempt);
            }
        }
        throw lastException;
    }

    private void sleepWithBackoff(int attempt) throws InterruptedException {
        long backoff = (long) Math.pow(2, attempt) * 5000;
        log.warn("API request failed (attempt {}/{}), retrying in {}ms", attempt + 1, MAX_RETRIES, backoff);
        Thread.sleep(backoff);
    }

    private List<Station> toStations(PublicBikeApiResponse response) {
        if (response == null || response.body() == null || response.body().item() == null) {
            return Collections.emptyList();
        }
        return response.body().item().stream()
                .map(this::toStation)
                .toList();
    }

    private Station toStation(Item item) {
        return new Station(
                item.rntstnId(),
                item.rntstnNm().trim(),
                Double.parseDouble(item.lat()),
                Double.parseDouble(item.lot()),
                Integer.parseInt(item.bcyclTpkctNocs())
        );
    }
}
