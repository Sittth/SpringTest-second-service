package spring.ru.secondservice.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import spring.ru.secondservice.models.ReceivedNotificationModel;

import java.util.UUID;

public interface ReceivedNotificationRepository extends JpaRepository<ReceivedNotificationModel, UUID> {

    @Modifying
    @Query(value = """
        INSERT INTO test_metadata.received_notifications (notification_id, recipient, message)
        VALUES (:notificationId, :recipient, :message)
        ON CONFLICT DO NOTHING
    """, nativeQuery = true)
    int insertIfAbsent(@Param("notificationId") UUID notificationId,
                       @Param("recipient") String recipient,
                       @Param("message") String message);
}
