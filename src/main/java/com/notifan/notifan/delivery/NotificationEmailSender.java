package com.notifan.notifan.delivery;

import com.notifan.notifan.notification.Notification;
import com.notifan.notifan.notification.NotificationStatusUpdater;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import jakarta.annotation.Nonnull;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationEmailSender {

    private final MailingService mailingService;
    private final NotificationStatusUpdater notificationStatusUpdater;

    @CircuitBreaker(name = "sendEmail", fallbackMethod = "sendEmailFallback")
    public void sendEmail(@NonNull Notification notification) {
        mailingService.send(notification);
        notificationStatusUpdater.markDelivered(notification, true);
    }

    public void sendEmailFallback(@Nonnull Notification notification, Exception e) {
        notificationStatusUpdater.markDelivered(notification, false);
    }
}
