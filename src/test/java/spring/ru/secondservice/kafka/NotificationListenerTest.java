package spring.ru.secondservice.kafka;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import spring.ru.secondservice.AbstractServiceTest;
import spring.ru.secondservice.repositories.ReceivedNotificationRepository;

import java.time.Duration;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

class NotificationListenerTest extends AbstractServiceTest {

    @Autowired
    KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    ReceivedNotificationRepository repository;

    @Value("${notification.topic}")
    String topic;

    @Value("${notification.dlt-topic}")
    String dltTopic;

    @Test
    void listener_shouldProcessDuplicateOnlyOnce() throws Exception {
        UUID duplicated = UUID.randomUUID();
        UUID marker = UUID.randomUUID();
        String key = "dedup-" + UUID.randomUUID();

        send(key, json(duplicated, "alice", "hello"));
        send(key, json(duplicated, "alice", "hello"));
        send(key, json(marker, "bob", "marker"));

        await().atMost(Duration.ofSeconds(30))
                .untilAsserted(() -> assertThat(repository.existsById(marker)).isTrue());

        assertThat(repository.count()).isEqualTo(2);
        assertThat(repository.existsById(duplicated)).isTrue();
    }

    @Test
    void listener_shouldSendMalformedJsonToDlt_withoutRetry() throws Exception {
        String garbage = "not-a-json-" + UUID.randomUUID();

        send("bad-json", garbage);

        ConsumerRecord<String, String> dltRecord = awaitRecord(dltTopic, r -> garbage.equals(r.value()));
        assertThat(dltRecord.value()).isEqualTo(garbage);
        assertThat(repository.count()).isZero();
    }

    @Test
    void listener_shouldSendEventWithMissingField_toDlt() throws Exception {
        UUID id = UUID.randomUUID();
        String withoutRecipient = "{\"notificationId\":\"" + id + "\",\"message\":\"hi\"}";

        send("missing-field", withoutRecipient);

        ConsumerRecord<String, String> dltRecord = awaitRecord(dltTopic, r -> withoutRecipient.equals(r.value()));
        assertThat(dltRecord.value()).isEqualTo(withoutRecipient);
        assertThat(repository.existsById(id)).isFalse();
    }

    private void send(String key, String payload) throws Exception {
        kafkaTemplate.send(topic, key, payload).get();
    }

    private String json(UUID id, String recipient, String message) {
        return "{\"notificationId\":\"" + id + "\",\"recipient\":\"" + recipient
                + "\",\"message\":\"" + message + "\"}";
    }
}