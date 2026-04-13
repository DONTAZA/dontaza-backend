package com.dontaza.dontazabackend.riding.dto;

import java.util.List;

public record CancelResponse(List<Long> cancelledRidingIds) {
}
