package com.notifan.notifan.delivery;

import com.notifan.notifan.notification.Notification;
import com.notifan.notifan.notification.NotificationStatusUpdater;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import jakarta.annotation.Nonnull;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Wraps {@link MailingService} with a circuit breaker. A separate class from
 * {@link NotificationDeliveryService} deliberately — Spring AOP proxies only intercept calls
 * from outside the declaring class, so sendEmail must be called externally, not
 * self-invoked, for the annotation to actually apply.
 */
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

    /**
     * Runs when the circuit is open or sendEmail throws. Requires the exception parameter
     *  even though unused — Resilience4j won't resolve a fallback without it.
     */
    public void sendEmailFallback(@Nonnull Notification notification, Exception e) {
        notificationStatusUpdater.markDelivered(notification, false);
    }
}
