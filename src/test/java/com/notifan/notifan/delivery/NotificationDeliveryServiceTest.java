package com.notifan.notifan.delivery;

import com.notifan.notifan.notification.EventType;
import com.notifan.notifan.notification.Notification;
import com.notifan.notifan.notification.NotificationRepository;
import com.notifan.notifan.notification.NotificationStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.MailSendException;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.Duration;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;

@SpringBootTest
public class NotificationDeliveryServiceTest {

    @Autowired
    private NotificationDeliveryService notificationDeliveryService;

    @Autowired
    private NotificationRepository notificationRepository;

    @MockitoBean
    private MailingService mailingService;

    @Test
    void deliverNotificationSuccessfully() {
        var savedNotification = notificationRepository.save(new Notification(UUID.randomUUID(), EventType.POST_LIKED));

        notificationDeliveryService.deliverAsync(savedNotification);

        await().atMost(Duration.ofSeconds(10)).untilAsserted(() -> {
            assertThat(notificationRepository.findById(savedNotification.getId()))
                    .isPresent()
                    .get()
                    .extracting(Notification::getStatus)
                    .isEqualTo(NotificationStatus.SENT);

        });
    }

    @Test
    void deliverNotificationFailed() {
        var savedNotification = notificationRepository.save(new Notification(UUID.randomUUID(), EventType.POST_LIKED));
        doThrow(new MailSendException("Simulated failure")).when(mailingService).send(any(Notification.class));

        notificationDeliveryService.deliverAsync(savedNotification);

        await().atMost(Duration.ofSeconds(10)).untilAsserted(() -> {
            assertThat(notificationRepository.findById(savedNotification.getId()))
                    .isPresent()
                    .get()
                    .extracting(Notification::getStatus)
                    .isEqualTo(NotificationStatus.FAILED);
        });
    }
}
