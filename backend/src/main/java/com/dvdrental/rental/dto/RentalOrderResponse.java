package com.dvdrental.rental.dto;

import com.dvdrental.rental.entity.RentalOrderStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record RentalOrderResponse(
        Long id,
        Long customerId,
        String customerEmail,
        String customerName,
        RentalOrderStatus status,
        BigDecimal totalAmount,
        Instant createdAt,
        List<RentalItemResponse> items
) {
}
