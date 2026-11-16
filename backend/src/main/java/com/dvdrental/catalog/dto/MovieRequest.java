package com.dvdrental.catalog.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.List;

public record MovieRequest(
        @NotBlank @Size(max = 255) String title,
        String description,
        Integer releaseYear,
        @NotNull @DecimalMin("0.00") BigDecimal dailyRate,
        @Min(1) Integer rentalDaysDefault,
        List<Long> genreIds
) {
}
