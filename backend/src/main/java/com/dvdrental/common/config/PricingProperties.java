package com.dvdrental.common.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.math.BigDecimal;

@ConfigurationProperties(prefix = "app.pricing")
public class PricingProperties {

    private BigDecimal overdueMultiplier = BigDecimal.ONE;

    public BigDecimal getOverdueMultiplier() {
        return overdueMultiplier;
    }

    public void setOverdueMultiplier(BigDecimal overdueMultiplier) {
        this.overdueMultiplier = overdueMultiplier;
    }
}
