package com.sumit.orderplatform.inventory.messaging;

import com.sumit.orderplatform.events.InventoryFailedEvent;
import com.sumit.orderplatform.events.InventoryReservedEvent;
import com.sumit.orderplatform.events.OrderCreatedEvent;
import com.sumit.orderplatform.events.Topics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.UUID;

@Component
public class InventoryEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(InventoryEventPublisher.class);

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public InventoryEventPublisher(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publishReserved(OrderCreatedEvent order) {
        InventoryReservedEvent event = new InventoryReservedEvent(
                order.orderId(),
                order.customerId(),
                order.productId(),
                order.quantity(),
                order.amount(),
                Instant.now());
        send(Topics.INVENTORY_RESERVED, order.orderId(), event);
    }

    public void publishFailed(UUID orderId, String reason) {
        InventoryFailedEvent event = new InventoryFailedEvent(orderId, reason, Instant.now());
        send(Topics.INVENTORY_FAILED, orderId, event);
    }

    private void send(String topic, UUID orderId, Object event) {
        String key = orderId.toString();
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
