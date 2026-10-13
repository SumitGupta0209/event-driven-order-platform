package com.sumit.orderplatform.order.messaging;

import com.sumit.orderplatform.events.OrderCancelledEvent;
import com.sumit.orderplatform.events.OrderConfirmedEvent;
import com.sumit.orderplatform.events.OrderCreatedEvent;
import com.sumit.orderplatform.events.Topics;
import com.sumit.orderplatform.order.domain.Order;
import com.sumit.orderplatform.order.outbox.OutboxWriter;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class OrderEventPublisher {

    private final OutboxWriter outboxWriter;

    public OrderEventPublisher(OutboxWriter outboxWriter) {
        this.outboxWriter = outboxWriter;
    }

    public void publishOrderCreated(Order order) {
        OrderCreatedEvent event = new OrderCreatedEvent(
                order.getId(),
                order.getCustomerId(),
                order.getProductId(),
                order.getQuantity(),
                order.getAmount(),
                order.getPaymentMethod(),
                order.getCreatedAt());
        outboxWriter.write(order.getId().toString(), Topics.ORDER_CREATED, event);
    }

    public void publishOrderConfirmed(Order order) {
        OrderConfirmedEvent event = new OrderConfirmedEvent(
                order.getId(),
                order.getCustomerId(),
                order.getAmount(),
                Instant.now());
        outboxWriter.write(order.getId().toString(), Topics.ORDER_CONFIRMED, event);
    }

    public void publishOrderCancelled(Order order) {
        OrderCancelledEvent event = new OrderCancelledEvent(
                order.getId(),
                order.getCustomerId(),
                order.getCancellationReason(),
                Instant.now());
        outboxWriter.write(order.getId().toString(), Topics.ORDER_CANCELLED, event);
    }
}
