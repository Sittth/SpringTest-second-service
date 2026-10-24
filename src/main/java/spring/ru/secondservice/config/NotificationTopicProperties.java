package spring.ru.secondservice.config;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "notification")
public record NotificationTopicProperties(
        @NotBlank String topic,
        @NotBlank String dltTopic,
        @Min(1) int topicReplicas,
        @Min(1) int topicPartitions,
        @Min(1) int dltTopicPartitions
) {
}