package com.notifan.notifan.delivery;

import com.notifan.notifan.notification.Notification;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * Entry point handlers call after persisting a Notification. Runs delivery off the Kafka
 * consumer thread, so a slow or failing mail service can't block partition throughput.
 */
@Service
@RequiredArgsConstructor
public class NotificationDeliveryService {

    private final NotificationEmailSender notificationEmailSender;

    @Async
    public void deliverAsync(@NonNull Notification notification) {
        notificationEmailSender.sendEmail(notification);
    }
}
