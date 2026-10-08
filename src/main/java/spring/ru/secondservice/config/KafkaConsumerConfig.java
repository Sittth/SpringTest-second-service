package spring.ru.secondservice.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.FixedBackOff;
import spring.ru.secondservice.exceptions.InvalidNotificationException;
import org.apache.kafka.common.TopicPartition;

@Configuration
public class KafkaConsumerConfig {

    @Bean
    public NewTopic notificationTopic(@Value("${notification.topic}") String name) {
        return TopicBuilder.name(name)
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic notificationDltTopic(@Value("${notification.dlt-topic}") String name) {
        return TopicBuilder.name(name)
                .partitions(1)
                .replicas(1)
                .build();
    }

    @Bean
    public DefaultErrorHandler kafkaErrorHandler(KafkaTemplate<String, String> kafkaTemplate,
                                                 @Value("${notification.dlt-topic}") String dltTopic) {

        DeadLetterPublishingRecoverer recoverer = new DeadLetterPublishingRecoverer(kafkaTemplate,
                (consumerRecord, e) -> new TopicPartition(dltTopic, -1));

        FixedBackOff fixedBackOff = new FixedBackOff(2000L, 2L);

        DefaultErrorHandler errorHandler = new DefaultErrorHandler(recoverer, fixedBackOff);

        errorHandler.setCommitRecovered(true);
        errorHandler.addNotRetryableExceptions(InvalidNotificationException.class);

        return errorHandler;
    }
}
