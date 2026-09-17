package com.notifan.notifan.config;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import java.time.Duration;

/**
 * Event deduplication cache settings, kept separate from other config as its own concern.
 */
@Configuration
@ConfigurationProperties(prefix = "application.event-deduplication")
@Validated
@Getter
@Setter
public class EventDeduplicationProperties {

    @NotNull
    private Duration cacheTtl;
}
