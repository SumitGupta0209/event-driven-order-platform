package com.sumit.orderplatform.order.messaging;

import com.sumit.orderplatform.events.OrderCancelledEvent;
import com.sumit.orderplatform.events.OrderConfirmedEvent;
import com.sumit.orderplatform.events.OrderCreatedEvent;
import com.sumit.orderplatform.events.Topics;
import com.sumit.orderplatform.order.domain.Order;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class OrderEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(OrderEventPublisher.class);

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public OrderEventPublisher(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publishOrderCreated(Order order) {
        OrderCreatedEvent event = new OrderCreatedEvent(
                order.getId(),
                order.getCustomerId(),
                order.getProductId(),
                order.getQuantity(),
                order.getAmount(),
                order.getCreatedAt());
        send(Topics.ORDER_CREATED, order.getId().toString(), event);
    }

    public void publishOrderConfirmed(Order order) {
        OrderConfirmedEvent event = new OrderConfirmedEvent(
                order.getId(),
                order.getCustomerId(),
                order.getAmount(),
                Instant.now());
        send(Topics.ORDER_CONFIRMED, order.getId().toString(), event);
    }

    public void publishOrderCancelled(Order order) {
        OrderCancelledEvent event = new OrderCancelledEvent(
                order.getId(),
                order.getCustomerId(),
                order.getCancellationReason(),
                Instant.now());
        send(Topics.ORDER_CANCELLED, order.getId().toString(), event);
    }

    private void send(String topic, String key, Object event) {
        kafkaTemplate.send(topic, key, event)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("Failed to publish {} for order {}", topic, key, ex);
                    } else {
                        log.info("Published {} key={} partition={} offset={}",
                                topic,
                                key,
                                result.getRecordMetadata().partition(),
                                result.getRecordMetadata().offset());
                    }
                });
    }
}
