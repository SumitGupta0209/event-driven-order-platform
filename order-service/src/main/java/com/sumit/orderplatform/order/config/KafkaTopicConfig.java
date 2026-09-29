package com.sumit.orderplatform.order.config;

import com.sumit.orderplatform.events.Topics;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

    @Bean
    public NewTopic orderCreatedTopic() {
        return TopicBuilder.name(Topics.ORDER_CREATED).partitions(3).replicas(1).build();
    }

    @Bean
    public NewTopic orderConfirmedTopic() {
        return TopicBuilder.name(Topics.ORDER_CONFIRMED).partitions(3).replicas(1).build();
    }

    @Bean
    public NewTopic orderCancelledTopic() {
        return TopicBuilder.name(Topics.ORDER_CANCELLED).partitions(3).replicas(1).build();
    }

    // Dead-letter topics for everything this service consumes:
    // inventory.reserved, inventory.failed, payment.completed, payment.failed
    @Bean
    public NewTopic inventoryReservedDlt() {
        return TopicBuilder.name(Topics.INVENTORY_RESERVED + ".DLT").partitions(3).replicas(1).build();
    }

    @Bean
    public NewTopic inventoryFailedDlt() {
        return TopicBuilder.name(Topics.INVENTORY_FAILED + ".DLT").partitions(3).replicas(1).build();
    }

    @Bean
    public NewTopic paymentCompletedDlt() {
        return TopicBuilder.name(Topics.PAYMENT_COMPLETED + ".DLT").partitions(3).replicas(1).build();
    }

    @Bean
    public NewTopic paymentFailedDlt() {
        return TopicBuilder.name(Topics.PAYMENT_FAILED + ".DLT").partitions(3).replicas(1).build();
    }
}
