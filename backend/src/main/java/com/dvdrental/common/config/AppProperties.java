package com.dvdrental.common.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({PricingProperties.class, CorsProperties.class, SeedProperties.class})
public class AppProperties {
}
