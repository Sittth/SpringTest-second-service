package spring.ru.secondservice.kafka;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;
import spring.ru.secondservice.exceptions.InvalidNotificationException;
import spring.ru.secondservice.kafka.event.NotificationEvent;
import spring.ru.secondservice.services.ReceivedNotificationService;
import tools.jackson.databind.json.JsonMapper;

import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationListener {

    private final ReceivedNotificationService receivedNotificationService;
    private final JsonMapper jsonMapper;
    private final Validator validator;

    @KafkaListener(topics = "${notification.topic}")
    public void onMessage(String payload, Acknowledgment acknowledgment) {

        NotificationEvent notificationEvent = parse(payload);
        validate(notificationEvent);

        log.info("Received notification with id: {}", notificationEvent.notificationId());

        receivedNotificationService.process(notificationEvent);

        acknowledgment.acknowledge();

        log.debug("Notification {} processed and acknowledged", notificationEvent.notificationId());
    }

    private NotificationEvent parse(String payload) {
        try {
            return jsonMapper.readValue(payload, NotificationEvent.class);
        } catch (Exception e) {
            throw new InvalidNotificationException("Failed to parse JSON into NotificationEvent", e);
        }
    }

    private void validate(NotificationEvent event) {
        if (event == null) {
            throw new InvalidNotificationException("Event is null");
        }

        Set<ConstraintViolation<NotificationEvent>> violations = validator.validate(event);

        if (!violations.isEmpty()) {
            String details = violations.stream()
                    .map(v -> v.getPropertyPath() + " " + v.getMessage())
                    .sorted()
                    .collect(Collectors.joining(", "));
            throw new InvalidNotificationException("Invalid notification: " + details);
        }
    }
}
