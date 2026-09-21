package com.notifan.notifan.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * Enables @Async processing — required for NotificationDeliveryService.deliverAsync to
 *  actually run off the calling (Kafka consumer) thread.
 */
@Configuration
@EnableAsync
public class AsyncConfig {
}
