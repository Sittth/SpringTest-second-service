package spring.ru.secondservice.kafka.event;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record NotificationEvent(
        @NotNull UUID notificationId,
        @NotBlank String recipient,
        @NotBlank String message) {}