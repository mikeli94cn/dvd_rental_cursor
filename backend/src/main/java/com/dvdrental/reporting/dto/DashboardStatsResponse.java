package com.dvdrental.reporting.dto;

public record DashboardStatsResponse(
        long totalMovies,
        long totalCopies,
        long availableCopies,
        long activeRentals,
        long overdueItems
) {
}
