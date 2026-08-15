package com.notifan.notifan.config;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

/**
 * Externalized application-specific configuration, kept separate from Spring's own
 * reserved spring.* namespace.
 */
@Configuration
@ConfigurationProperties(prefix = "application")
@Validated
@Getter
@Setter
public class ApplicationProperties {

    @NotBlank
    private String platformEventsTopic;
}
