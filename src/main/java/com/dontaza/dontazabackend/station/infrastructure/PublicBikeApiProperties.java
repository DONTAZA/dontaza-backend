package com.dontaza.dontazabackend.station.infrastructure;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@ConfigurationProperties(prefix = "public-bike.api")
public record PublicBikeApiProperties(
        String baseUrl,
        String serviceKey,
        List<String> regionCodes
) {
}
