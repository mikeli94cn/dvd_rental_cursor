package com.dvdrental.rental.dto;

import com.dvdrental.rental.entity.RentalItemStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

public record RentalItemResponse(
        Long id,
        Long dvdCopyId,
        String barcode,
        Long movieId,
        String movieTitle,
        LocalDate dueDate,
        Instant returnedAt,
        BigDecimal lineAmount,
        BigDecimal overdueAmount,
        RentalItemStatus status
) {
}
