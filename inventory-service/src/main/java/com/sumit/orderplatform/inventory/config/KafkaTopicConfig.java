package com.sumit.orderplatform.inventory.config;

import com.sumit.orderplatform.events.Topics;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

    @Bean
    public NewTopic inventoryReservedTopic() {
        return TopicBuilder.name(Topics.INVENTORY_RESERVED).partitions(3).replicas(1).build();
    }

    @Bean
    public NewTopic inventoryFailedTopic() {
        return TopicBuilder.name(Topics.INVENTORY_FAILED).partitions(3).replicas(1).build();
    }

    // Dead-letter topics for everything this service consumes:
    // order.created and payment.failed.
    @Bean
    public NewTopic orderCreatedDlt() {
        return TopicBuilder.name(Topics.ORDER_CREATED + ".DLT").partitions(3).replicas(1).build();
    }

    @Bean
    public NewTopic paymentFailedDlt() {
        return TopicBuilder.name(Topics.PAYMENT_FAILED + ".DLT").partitions(3).replicas(1).build();
    }
}
