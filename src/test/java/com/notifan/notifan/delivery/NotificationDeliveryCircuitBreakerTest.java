package com.notifan.notifan.delivery;

import com.notifan.notifan.notification.EventType;
import com.notifan.notifan.notification.Notification;
import com.notifan.notifan.notification.NotificationRepository;
import com.notifan.notifan.notification.NotificationStatus;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.MailSendException;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.Duration;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.clearInvocations;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@SpringBootTest
@TestPropertySource(properties = "resilience4j.circuitbreaker.instances.sendEmail.wait-duration-in-open-state=2s")
class NotificationDeliveryCircuitBreakerTest {
    @Autowired
    private NotificationDeliveryService notificationDeliveryService;

    @Autowired
    private NotificationRepository notificationRepository;

    @MockitoBean
    private MailingService mailingService;

    @Autowired
    private CircuitBreakerRegistry circuitBreakerRegistry;

    private CircuitBreaker circuitBreaker;

    @BeforeEach
    void setUp() {
        circuitBreaker = circuitBreakerRegistry.circuitBreaker("sendEmail");
        circuitBreaker.reset();
    }

    /** Sends one notification, waits until its status is no longer PENDING, and returns it. */
    private Notification sendAndAwaitResolution(boolean shouldSucceed) {
        if (shouldSucceed) {
            doNothing().when(mailingService).send(any());
        } else {
            doThrow(new MailSendException("Simulated failure")).when(mailingService).send(any());
        }

        var notification = notificationRepository.save(new Notification(UUID.randomUUID(), EventType.POST_LIKED));
        notificationDeliveryService.deliverAsync(notification);

        await().atMost(Duration.ofSeconds(10)).untilAsserted(() ->
                assertThat(notificationRepository.findById(notification.getId()))
                        .get()
                        .extracting(Notification::getStatus)
                        .isNotEqualTo(NotificationStatus.PENDING));

        return notification;
    }

    @Test
    void opensCircuitAfterFailureThresholdAndRejectsSubsequentCalls() {
        for (int i = 0; i < 5; i++) sendAndAwaitResolution(true);
        for (int i = 0; i < 5; i++) sendAndAwaitResolution(false);

        assertThat(circuitBreaker.getState()).isEqualTo(CircuitBreaker.State.OPEN);

        // Circuit is open — the 11th call should be short-circuited, mailingService never invoked.
        clearInvocations(mailingService);
        doNothing().when(mailingService).send(any());
        var rejected = sendAndAwaitResolution(true);

        assertThat(notificationRepository.findById(rejected.getId()))
                .get()
                .extracting(Notification::getStatus)
                .isEqualTo(NotificationStatus.FAILED);

        verify(mailingService, never()).send(any());
    }

    @Test
    void closesCircuitWhenHalfOpenTrialMostlySucceeds() throws InterruptedException {
        for (int i = 0; i < 5; i++) sendAndAwaitResolution(true);
        for (int i = 0; i < 5; i++) sendAndAwaitResolution(false);
        assertThat(circuitBreaker.getState()).isEqualTo(CircuitBreaker.State.OPEN);

        Thread.sleep(2_500); // past wait-duration-in-open-state, moves to HALF_OPEN

        // 3 permitted trial calls, all succeed -> circuit should close.
        for (int i = 0; i < 3; i++) sendAndAwaitResolution(true);

        assertThat(circuitBreaker.getState()).isEqualTo(CircuitBreaker.State.CLOSED);
    }

    @Test
    void reopensCircuitWhenHalfOpenTrialMostlyFails() throws InterruptedException {
        for (int i = 0; i < 5; i++) sendAndAwaitResolution(true);
        for (int i = 0; i < 5; i++) sendAndAwaitResolution(false);
        assertThat(circuitBreaker.getState()).isEqualTo(CircuitBreaker.State.OPEN);

        Thread.sleep(2_500);

        // 3 permitted trial calls, 2/3 fail (66% > 50% threshold) → circuit reopens.
        sendAndAwaitResolution(true);
        sendAndAwaitResolution(false);
        sendAndAwaitResolution(false);

        assertThat(circuitBreaker.getState()).isEqualTo(CircuitBreaker.State.OPEN);
    }
}
