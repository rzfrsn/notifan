package com.notifan.notifan.notification;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "notifications")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Notification {
    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "recipient_id", nullable = false)
    private UUID recipientId;

    @Enumerated(EnumType.STRING)
    @Column(name = "event_type", nullable = false, length = 30)
    private EventType eventType;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    @Setter
    private NotificationStatus status = NotificationStatus.PENDING;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    @Column(name = "sent_at")
    @Setter
    private Instant sentAt;

    @Column(name = "retry_count", nullable = false)
    private int retryCount = 0;

    public Notification(UUID recipientId, EventType eventType) {
        this.recipientId = recipientId;
        this.eventType = eventType;
    }

    public void incrementRetryCount() {
        this.retryCount++;
    }
}
