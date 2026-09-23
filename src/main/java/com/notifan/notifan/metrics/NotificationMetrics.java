package com.notifan.notifan.metrics;

import org.springframework.stereotype.Service;

import com.notifan.notifan.notification.NotificationStatus;

import io.micrometer.core.instrument.MeterRegistry;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * Owns the notifications_total counter, tagged by status.
 */
@Service
@RequiredArgsConstructor
public class NotificationMetrics {

  private final MeterRegistry meterRegistry;

  public void record(@NonNull NotificationStatus status) {
    meterRegistry.counter("notifications_total", "status", status.name()).increment();
  }

}
