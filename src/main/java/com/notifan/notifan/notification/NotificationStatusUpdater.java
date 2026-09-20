package com.notifan.notifan.notification;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationStatusUpdater {

    private final NotificationRepository notificationRepository;

    public void markDelivered(@NonNull Notification notification, boolean isDelivered) {
        notification.setStatus(isDelivered ? NotificationStatus.SENT : NotificationStatus.FAILED);
        notificationRepository.save(notification);
    }
}
