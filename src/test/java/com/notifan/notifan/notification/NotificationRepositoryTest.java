package com.notifan.notifan.notification;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class NotificationRepositoryTest {
    @Autowired
    private NotificationRepository notificationRepository;

    @Test
    void savesAndRetrievesNotification() {
        UUID recipientId = UUID.randomUUID();
        Notification notification = new Notification(recipientId, EventType.POST_LIKED);

        Notification saved = notificationRepository.save(notification);

        assertThat(saved.getId()).isNotNull();
        assertThat(notificationRepository.findById(saved.getId()))
                .isPresent()
                .get()
                .extracting(Notification::getStatus)
                .isEqualTo(NotificationStatus.PENDING);
    }
}
