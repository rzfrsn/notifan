package com.notifan.notifan.notification;

import org.springframework.stereotype.Service;

import com.notifan.notifan.metrics.NotificationMetrics;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * Owns the SENT/FAILED status transition after a delivery attempt
 */
@Service
@RequiredArgsConstructor
public class NotificationStatusUpdater {

    private final NotificationRepository notificationRepository;
    private final NotificationMetrics notificationMetrics;

    public void markDelivered(@NonNull Notification notification, boolean isDelivered) {
        var status = isDelivered ? NotificationStatus.SENT : NotificationStatus.FAILED;
        notification.setStatus(isDelivered ? NotificationStatus.SENT : NotificationStatus.FAILED);
        notificationRepository.save(notification);

        notificationMetrics.record(status);
    }
}
