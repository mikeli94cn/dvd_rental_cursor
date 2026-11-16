package com.dvdrental.pricing;

import com.dvdrental.common.config.PricingProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PricingServiceTest {

    private PricingService pricingService;

    @BeforeEach
    void setUp() {
        PricingProperties properties = new PricingProperties();
        properties.setOverdueMultiplier(new BigDecimal("1.0"));
        pricingService = new PricingService(properties);
    }

    @Test
    void calculatesRentalCharge() {
        assertThat(pricingService.calculateRentalCharge(new BigDecimal("2.99"), 3))
                .isEqualByComparingTo("8.97");
    }

    @Test
    void rejectsInvalidRentalDays() {
        assertThatThrownBy(() -> pricingService.calculateRentalCharge(new BigDecimal("2.00"), 0))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void overdueIsZeroWhenReturnedOnTime() {
        assertThat(pricingService.calculateOverdueCharge(
                new BigDecimal("2.00"),
                LocalDate.of(2026, 1, 10),
                LocalDate.of(2026, 1, 10)
        )).isEqualByComparingTo("0.00");
    }

    @Test
    void calculatesOverdueCharge() {
        assertThat(pricingService.calculateOverdueCharge(
                new BigDecimal("2.50"),
                LocalDate.of(2026, 1, 1),
                LocalDate.of(2026, 1, 4)
        )).isEqualByComparingTo("7.50");
    }

    @Test
    void appliesOverdueMultiplier() {
        PricingProperties properties = new PricingProperties();
        properties.setOverdueMultiplier(new BigDecimal("1.5"));
        pricingService = new PricingService(properties);

        assertThat(pricingService.calculateOverdueCharge(
                new BigDecimal("2.00"),
                LocalDate.of(2026, 1, 1),
                LocalDate.of(2026, 1, 3)
        )).isEqualByComparingTo("6.00");
    }
}
