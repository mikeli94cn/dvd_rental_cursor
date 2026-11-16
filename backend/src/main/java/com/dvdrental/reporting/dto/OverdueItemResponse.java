package com.dvdrental.reporting.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record OverdueItemResponse(
        Long rentalItemId,
        Long rentalOrderId,
        String customerEmail,
        String customerName,
        String movieTitle,
        String barcode,
        LocalDate dueDate,
        long overdueDays,
        BigDecimal estimatedOverdueCharge
) {
}
