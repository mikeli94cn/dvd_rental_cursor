package com.dvdrental.catalog.dto;

import java.math.BigDecimal;
import java.util.List;

public record MovieResponse(
        Long id,
        String title,
        String description,
        Integer releaseYear,
        BigDecimal dailyRate,
        int rentalDaysDefault,
        List<GenreResponse> genres,
        long availableCopies
) {
}
