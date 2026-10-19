package com.sumit.orderplatform.inventory.messaging;

import com.sumit.orderplatform.events.InventoryFailedEvent;
import com.sumit.orderplatform.events.InventoryReservedEvent;
import com.sumit.orderplatform.events.OrderCreatedEvent;
import com.sumit.orderplatform.events.Topics;
import com.sumit.orderplatform.inventory.outbox.OutboxWriter;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.UUID;

@Component
public class InventoryEventPublisher {

    private final OutboxWriter outboxWriter;

    public InventoryEventPublisher(OutboxWriter outboxWriter) {
        this.outboxWriter = outboxWriter;
    }

    public void publishReserved(OrderCreatedEvent order) {
        InventoryReservedEvent event = new InventoryReservedEvent(
                order.orderId(),
                order.customerId(),
                order.productId(),
                order.quantity(),
                order.amount(),
                order.paymentMethod(),
                Instant.now());
        outboxWriter.write(order.orderId().toString(), Topics.INVENTORY_RESERVED, event);
    }

    public void publishFailed(UUID orderId, String reason) {
        InventoryFailedEvent event = new InventoryFailedEvent(orderId, reason, Instant.now());
        outboxWriter.write(orderId.toString(), Topics.INVENTORY_FAILED, event);
    }
}
