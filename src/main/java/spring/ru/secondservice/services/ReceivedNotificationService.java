package spring.ru.secondservice.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import spring.ru.secondservice.kafka.event.NotificationEvent;
import spring.ru.secondservice.repositories.ReceivedNotificationRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReceivedNotificationService {

    private final ReceivedNotificationRepository receivedNotificationRepository;

    @Transactional
    public boolean process(NotificationEvent event) {
        log.info("Received notification with id: {}", event.notificationId());

        int inserted = receivedNotificationRepository.insertIfAbsent(
                event.notificationId(),
                event.recipient(),
                event.message()
        );

        boolean isNew = inserted != 0;

        log.info("notificationId={}, isNew={}", event.notificationId(), isNew);

        return isNew;
    }
}
