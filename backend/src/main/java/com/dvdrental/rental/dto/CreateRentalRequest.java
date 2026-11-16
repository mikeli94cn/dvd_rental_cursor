package com.dvdrental.rental.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record CreateRentalRequest(
        @NotEmpty List<@NotNull Long> dvdCopyIds,
        @Min(1) Integer rentalDays
) {
}
