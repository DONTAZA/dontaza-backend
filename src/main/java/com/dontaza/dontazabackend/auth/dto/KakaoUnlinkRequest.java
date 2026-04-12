package com.dontaza.dontazabackend.auth.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record KakaoUnlinkRequest(
        @JsonProperty("user_id") Long userId,
        @JsonProperty("referrer_type") String referrerType
) {
}
