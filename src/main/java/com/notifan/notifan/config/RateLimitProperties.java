package com.notifan.notifan.config;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import java.time.Duration;

/**
 * Sliding-window rate limit settings, kept separate from {@link ApplicationProperties} since
 * this is its own cohesive concern, not a general application setting.
 */
@Configuration
@ConfigurationProperties(prefix = "application.rate-limit")
@Validated
@Getter
@Setter
public class RateLimitProperties {

    @NotNull
    @Positive
    private Integer maxRequests;

    @NotNull
    private Duration window;
}
