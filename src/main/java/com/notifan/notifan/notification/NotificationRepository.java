package com.notifan.notifan.notification;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface NotificationRepository extends JpaRepository<Notification, UUID> {

    /**
     * Finds every notification for the given recipients — used to verify fan-out results,
     * where one {@link com.notifan.notifan.event.CommentAddedEvent} produces one row per
     * recipient.
     *
     * @param recipientIds the recipient ids to match
     * @return all notifications whose recipientId is in the given list
     */
    List<Notification> findByRecipientIdIn(List<UUID> recipientIds);

}
