package spring.ru.secondservice.kafka.event;

import java.util.UUID;

public record NotificationEvent(UUID notificationId, String recipient, String message) {}