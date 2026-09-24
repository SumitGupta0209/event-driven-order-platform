package com.sumit.orderplatform.order.messaging;

import com.sumit.orderplatform.events.InventoryFailedEvent;
import com.sumit.orderplatform.events.InventoryReservedEvent;
import com.sumit.orderplatform.events.PaymentCompletedEvent;
import com.sumit.orderplatform.events.PaymentFailedEvent;
import com.sumit.orderplatform.events.Topics;
import com.sumit.orderplatform.order.domain.Order;
import com.sumit.orderplatform.order.service.OrderStatusService;
import com.sumit.orderplatform.order.service.TransitionResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class OrderSagaListener {

    private static final Logger log = LoggerFactory.getLogger(OrderSagaListener.class);

    private final OrderStatusService statusService;
    private final OrderEventPublisher publisher;

    public OrderSagaListener(OrderStatusService statusService, OrderEventPublisher publisher) {
        this.statusService = statusService;
        this.publisher = publisher;
    }

    @KafkaListener(topics = Topics.INVENTORY_RESERVED)
    public void onInventoryReserved(InventoryReservedEvent event) {
        log.info("Received inventory.reserved orderId={}", event.orderId());
        afterTransition(Topics.INVENTORY_RESERVED, event.orderId(),
                statusService.inventoryReserved(event.orderId()));
    }

    @KafkaListener(topics = Topics.INVENTORY_FAILED)
    public void onInventoryFailed(InventoryFailedEvent event) {
        log.info("Received inventory.failed orderId={} reason={}", event.orderId(), event.reason());
        afterTransition(Topics.INVENTORY_FAILED, event.orderId(),
                statusService.inventoryFailed(event.orderId(), event.reason()));
    }

    @KafkaListener(topics = Topics.PAYMENT_COMPLETED)
    public void onPaymentCompleted(PaymentCompletedEvent event) {
        log.info("Received payment.completed orderId={}", event.orderId());
        afterTransition(Topics.PAYMENT_COMPLETED, event.orderId(),
                statusService.paymentCompleted(event.orderId()));
    }

    @KafkaListener(topics = Topics.PAYMENT_FAILED)
    public void onPaymentFailed(PaymentFailedEvent event) {
        log.info("Received payment.failed orderId={} reason={}", event.orderId(), event.reason());
        afterTransition(Topics.PAYMENT_FAILED, event.orderId(),
                statusService.paymentFailed(event.orderId(), event.reason()));
    }

    private void afterTransition(String source, UUID orderId, TransitionResult result) {
        switch (result.outcome()) {
            case NOT_FOUND -> log.warn("{}: order {} not found, ignoring", source, orderId);
            case UNCHANGED -> log.info("{}: order {} stays {}", source, orderId, result.order().getStatus());
            case CHANGED -> {
                Order order = result.order();
                log.info("{}: order {} is now {}", source, orderId, order.getStatus());
                switch (order.getStatus()) {
                    case CONFIRMED -> publisher.publishOrderConfirmed(order);
                    case CANCELLED -> publisher.publishOrderCancelled(order);
                    default -> { }
                }
            }
        }
    }
}
