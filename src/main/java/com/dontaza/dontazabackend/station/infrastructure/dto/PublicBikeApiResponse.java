package com.dontaza.dontazabackend.station.infrastructure.dto;

import java.util.List;

public record PublicBikeApiResponse(Header header, Body body) {

    public record Header(String resultCode, String resultMsg) {
    }

    public record Body(
            String totalCount,
            List<Item> item,
            String numOfRows,
            String pageNo
    ) {
    }

    public record Item(
            String rntstnId,
            String rntstnNm,
            String lat,
            String lot,
            String bcyclTpkctNocs,
            String lcgvmnInstCd,
            String lcgvmnInstNm
    ) {
    }
}
