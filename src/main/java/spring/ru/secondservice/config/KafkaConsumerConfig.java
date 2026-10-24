package spring.ru.secondservice.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.TopicPartition;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.FixedBackOff;
import spring.ru.secondservice.exceptions.InvalidPayloadException;

@Configuration
@EnableConfigurationProperties(NotificationTopicProperties.class)
public class KafkaConsumerConfig {

    @Bean
    public NewTopic notificationTopic(NotificationTopicProperties properties) {
        return TopicBuilder.name(properties.topic())
                .partitions(properties.topicPartitions())
                .replicas(properties.topicReplicas())
                .build();
    }

    @Bean
    public NewTopic notificationDltTopic(NotificationTopicProperties properties) {
        return TopicBuilder.name(properties.dltTopic())
                .partitions(properties.dltTopicPartitions())
                .replicas(properties.topicReplicas())
                .build();
    }

    @Bean
    public DefaultErrorHandler kafkaErrorHandler(KafkaTemplate<String, String> kafkaTemplate,
                                                 NotificationTopicProperties properties) {

        DeadLetterPublishingRecoverer recoverer = new DeadLetterPublishingRecoverer(kafkaTemplate,
                (consumerRecord, e) -> new TopicPartition(properties.dltTopic(), -1));

        FixedBackOff fixedBackOff = new FixedBackOff(2000L, 2L);

        DefaultErrorHandler errorHandler = new DefaultErrorHandler(recoverer, fixedBackOff);

        errorHandler.setCommitRecovered(true);
        errorHandler.addNotRetryableExceptions(InvalidPayloadException.class);

        return errorHandler;
    }
}
