package com.dontaza.dontazabackend.station.infrastructure;

import com.dontaza.dontazabackend.station.domain.Station;
import com.dontaza.dontazabackend.station.infrastructure.dto.PublicBikeApiResponse;
import com.dontaza.dontazabackend.station.infrastructure.dto.PublicBikeApiResponse.Item;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class PublicBikeApiClient {

    private static final String CACHE_KEY = "all";
    private static final int PAGE_SIZE = 1000;

    private final PublicBikeApiProperties properties;
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final LoadingCache<String, List<Station>> stationCache;

    public PublicBikeApiClient(PublicBikeApiProperties properties, ObjectMapper objectMapper) {
        this.properties = properties;
        this.httpClient = HttpClient.newHttpClient();
        this.objectMapper = objectMapper;
        this.stationCache = Caffeine.newBuilder()
                .refreshAfterWrite(5, TimeUnit.MINUTES)
                .expireAfterWrite(10, TimeUnit.MINUTES)
                .build(key -> loadAllStations());
    }

    @PostConstruct
    void warmUp() {
        try {
            stationCache.get(CACHE_KEY);
            log.info("Station cache warmed up: {} stations", stationCache.get(CACHE_KEY).size());
        } catch (Exception e) {
            log.warn("Station cache warm-up failed: {}", e.getMessage());
        }
    }

    public List<Station> fetchAllStations() {
        List<Station> stations = stationCache.get(CACHE_KEY);
        return stations != null ? stations : Collections.emptyList();
    }

    private List<Station> loadAllStations() {
        return properties.regionCodes().stream()
                .flatMap(code -> fetchStationsByRegion(code).stream())
                .toList();
    }

    private List<Station> fetchStationsByRegion(String regionCode) {
        List<Station> result = new ArrayList<>();
        int pageNo = 1;

        try {
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
            log.info("Fetched {} stations for region {}", result.size(), regionCode);
        } catch (Exception e) {
            log.warn("Failed to fetch stations for region {}: {}", regionCode, e.getMessage());
        }
        return result;
    }

    private PublicBikeApiResponse requestPage(String regionCode, int pageNo) throws Exception {
        String query = "serviceKey=" + properties.serviceKey()
                + "&pageNo=" + pageNo
                + "&numOfRows=" + PAGE_SIZE
                + "&type=json&lcgvmnInstCd=" + regionCode;
        URI uri = new URI("https", "apis.data.go.kr",
                "/B551982/pbdo_v2/inf_101_00010002_v2", query, null);

        HttpResponse<String> response = httpClient.send(
                HttpRequest.newBuilder().uri(uri).GET().build(),
                HttpResponse.BodyHandlers.ofString());
        return objectMapper.readValue(response.body(), PublicBikeApiResponse.class);
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
                item.rntstnNm(),
                Double.parseDouble(item.lat()),
                Double.parseDouble(item.lot()),
                Integer.parseInt(item.bcyclTpkctNocs())
        );
    }
}
