package com.sumit.orderplatform.order.messaging;

import com.sumit.orderplatform.events.InventoryFailedEvent;
import com.sumit.orderplatform.events.InventoryReservedEvent;
import com.sumit.orderplatform.events.PaymentCompletedEvent;
import com.sumit.orderplatform.events.PaymentFailedEvent;
import com.sumit.orderplatform.events.Topics;
import com.sumit.orderplatform.order.domain.Order;
import com.sumit.orderplatform.order.service.IdempotencyService;
import com.sumit.orderplatform.order.service.OrderStatusService;
import com.sumit.orderplatform.order.service.TransitionResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class OrderSagaListener {

    private static final Logger log = LoggerFactory.getLogger(OrderSagaListener.class);

    private final OrderStatusService statusService;
    private final OrderEventPublisher publisher;
    private final IdempotencyService idempotencyService;

    public OrderSagaListener(OrderStatusService statusService,
                             OrderEventPublisher publisher,
                             IdempotencyService idempotencyService) {
        this.statusService = statusService;
        this.publisher = publisher;
        this.idempotencyService = idempotencyService;
    }

    @KafkaListener(topics = Topics.INVENTORY_RESERVED)
    public void onInventoryReserved(
            InventoryReservedEvent event,
            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset) {
        if (!seenFirstTime(topic, partition, offset, event.orderId())) {
            return;
        }
        log.info("Received inventory.reserved orderId={}", event.orderId());
        afterTransition(Topics.INVENTORY_RESERVED, event.orderId(),
                statusService.inventoryReserved(event.orderId()));
    }

    @KafkaListener(topics = Topics.INVENTORY_FAILED)
    public void onInventoryFailed(
            InventoryFailedEvent event,
            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset) {
        if (!seenFirstTime(topic, partition, offset, event.orderId())) {
            return;
        }
        log.info("Received inventory.failed orderId={} reason={}", event.orderId(), event.reason());
        afterTransition(Topics.INVENTORY_FAILED, event.orderId(),
                statusService.inventoryFailed(event.orderId(), event.reason()));
    }

    @KafkaListener(topics = Topics.PAYMENT_COMPLETED)
    public void onPaymentCompleted(
            PaymentCompletedEvent event,
            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset) {
        if (!seenFirstTime(topic, partition, offset, event.orderId())) {
            return;
        }
        log.info("Received payment.completed orderId={}", event.orderId());
        afterTransition(Topics.PAYMENT_COMPLETED, event.orderId(),
                statusService.paymentCompleted(event.orderId()));
    }

    @KafkaListener(topics = Topics.PAYMENT_FAILED)
    public void onPaymentFailed(
            PaymentFailedEvent event,
            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset) {
        if (!seenFirstTime(topic, partition, offset, event.orderId())) {
            return;
        }
        log.info("Received payment.failed orderId={} reason={}", event.orderId(), event.reason());
        afterTransition(Topics.PAYMENT_FAILED, event.orderId(),
                statusService.paymentFailed(event.orderId(), event.reason()));
    }

    private boolean seenFirstTime(String topic, int partition, long offset, UUID orderId) {
        String messageKey = topic + "-" + partition + "-" + offset;
        if (!idempotencyService.markProcessed(messageKey)) {
            log.info("Duplicate delivery of {} ignored (orderId={})", messageKey, orderId);
            return false;
        }
        return true;
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
