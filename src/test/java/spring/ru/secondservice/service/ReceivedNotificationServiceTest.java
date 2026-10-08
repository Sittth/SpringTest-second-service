package spring.ru.secondservice.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import spring.ru.secondservice.AbstractServiceTest;
import spring.ru.secondservice.kafka.event.NotificationEvent;
import spring.ru.secondservice.repositories.ReceivedNotificationRepository;
import spring.ru.secondservice.services.ReceivedNotificationService;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ReceivedNotificationServiceTest extends AbstractServiceTest {

    @Autowired
    ReceivedNotificationService service;

    @Autowired
    ReceivedNotificationRepository repository;

    @Test
    void process_shouldReturnTrue_forNewNotification() {
        boolean isNew = service.process(new NotificationEvent(UUID.randomUUID(), "bob", "hello"));

        assertThat(isNew).isTrue();
        assertThat(repository.count()).isEqualTo(1);
    }

    @Test
    void process_shouldIgnoreDuplicate_andKeepSingleRow() {
        NotificationEvent event = new NotificationEvent(UUID.randomUUID(), "bob", "hello");

        boolean first = service.process(event);
        boolean second = service.process(event);

        assertThat(first).isTrue();
        assertThat(second).isFalse();
        assertThat(repository.count()).isEqualTo(1);
    }
}