package spring.ru.secondservice.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "received_notifications")
@Getter
@Setter
public class ReceivedNotificationModel {

    @Id
    private UUID notificationId;

    @Column(nullable = false)
    private String recipient;

    @Column(nullable = false)
    private String message;

    @Column
    private OffsetDateTime receivedAt;
}
