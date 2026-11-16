package com.dvdrental.pricing;

import com.dvdrental.common.config.PricingProperties;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Service
public class PricingService {

    private final PricingProperties pricingProperties;

    public PricingService(PricingProperties pricingProperties) {
        this.pricingProperties = pricingProperties;
    }

    public BigDecimal calculateRentalCharge(BigDecimal dailyRate, int rentalDays) {
        if (dailyRate == null || dailyRate.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("dailyRate must be non-negative");
        }
        if (rentalDays < 1) {
            throw new IllegalArgumentException("rentalDays must be at least 1");
        }
        return dailyRate.multiply(BigDecimal.valueOf(rentalDays)).setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal calculateOverdueCharge(BigDecimal dailyRate, LocalDate dueDate, LocalDate returnDate) {
        if (dailyRate == null || dueDate == null || returnDate == null) {
            throw new IllegalArgumentException("dailyRate, dueDate and returnDate are required");
        }
        long overdueDays = ChronoUnit.DAYS.between(dueDate, returnDate);
        if (overdueDays <= 0) {
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }
        BigDecimal multiplier = pricingProperties.getOverdueMultiplier() == null
                ? BigDecimal.ONE
                : pricingProperties.getOverdueMultiplier();
        return dailyRate
                .multiply(BigDecimal.valueOf(overdueDays))
                .multiply(multiplier)
                .setScale(2, RoundingMode.HALF_UP);
    }

    public long overdueDays(LocalDate dueDate, LocalDate returnDate) {
        long days = ChronoUnit.DAYS.between(dueDate, returnDate);
        return Math.max(0, days);
    }
}
