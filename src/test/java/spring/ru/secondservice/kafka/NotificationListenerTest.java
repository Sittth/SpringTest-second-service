package spring.ru.secondservice.kafka;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.Acknowledgment;
import spring.ru.secondservice.AbstractServiceTest;
import spring.ru.secondservice.repositories.ReceivedNotificationRepository;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class NotificationListenerTest extends AbstractServiceTest {

    @Autowired
    KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    ReceivedNotificationRepository repository;

    @Autowired
    NotificationListener listener;

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
    void listener_shouldProcessSameNotificationOnce_whenDeliveredConcurrentlyToDifferentConsumers()
            throws Exception {
        int consumers = 8;
        UUID id = UUID.randomUUID();
        String payload = json(id, "alice", "hello");

        ExecutorService executor = Executors.newFixedThreadPool(consumers);
        CountDownLatch ready = new CountDownLatch(consumers);
        CountDownLatch start = new CountDownLatch(1);
        List<Acknowledgment> acknowledgments = new ArrayList<>();

        try {
            List<Future<Void>> futures = new ArrayList<>();
            for (int i = 0; i < consumers; i++) {
                Acknowledgment acknowledgment = mock(Acknowledgment.class);
                acknowledgments.add(acknowledgment);

                futures.add(executor.submit(() -> {
                    ready.countDown();
                    start.await();
                    listener.onMessage(payload, acknowledgment);
                    return null;
                }));
            }

            ready.await();
            start.countDown();

            for (Future<Void> future : futures) {
                future.get(30, TimeUnit.SECONDS);
            }
        } finally {
            executor.shutdownNow();
        }

        assertThat(repository.count()).isEqualTo(1);
        assertThat(repository.existsById(id)).isTrue();
        acknowledgments.forEach(acknowledgment -> verify(acknowledgment, times(1)).acknowledge());
    }

    @Test
    void listener_shouldSendMalformedJsonToDlt_withoutBlockingNextMessages() throws Exception {
        String garbage = "not-a-json-" + UUID.randomUUID();
        UUID next = UUID.randomUUID();
        String key = "poison-" + UUID.randomUUID();

        send(key, garbage);
        send(key, json(next, "carol", "after the poison message"));

        ConsumerRecord<String, String> dltRecord = awaitRecord(dltTopic, r -> garbage.equals(r.value()));
        assertThat(dltRecord.value()).isEqualTo(garbage);

        await().atMost(Duration.ofSeconds(30))
                .untilAsserted(() -> assertThat(repository.existsById(next)).isTrue());

        assertThat(repository.count()).isEqualTo(1);
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