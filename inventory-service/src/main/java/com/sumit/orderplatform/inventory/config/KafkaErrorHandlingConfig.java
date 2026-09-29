package com.sumit.orderplatform.inventory.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaOperations;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.FixedBackOff;

@Configuration
public class KafkaErrorHandlingConfig {

    private static final Logger log = LoggerFactory.getLogger(KafkaErrorHandlingConfig.class);

    @Bean
    public DefaultErrorHandler kafkaErrorHandler(KafkaOperations<Object, Object> kafkaOperations) {
        // Explicitly force "<topic>.DLT" naming (same partition number preserved),
        // matching the NewTopic beans we declared ourselves. Without this,
        // Spring Kafka's default resolver uses "<topic>-dlt" (lowercase, hyphen)
        // instead, which silently misses our declared topic entirely.
        DeadLetterPublishingRecoverer recoverer = new DeadLetterPublishingRecoverer(
                kafkaOperations,
                (record, ex) -> new org.apache.kafka.common.TopicPartition(
                        record.topic() + ".DLT", record.partition()));

        DefaultErrorHandler handler = new DefaultErrorHandler(recoverer, new FixedBackOff(1000L, 3));

        handler.setRetryListeners((record, ex, deliveryAttempt) ->
                log.warn("Retry {} for {}-{}@{}: {}",
                        deliveryAttempt, record.topic(), record.partition(), record.offset(), ex.getMessage()));

        return handler;
    }
}
