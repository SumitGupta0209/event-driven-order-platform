package com.sumit.orderplatform.inventory.messaging;

import com.sumit.orderplatform.events.PaymentFailedEvent;
import com.sumit.orderplatform.events.Topics;
import com.sumit.orderplatform.inventory.service.IdempotencyService;
import com.sumit.orderplatform.inventory.service.InventoryService;
import com.sumit.orderplatform.inventory.service.ReleaseResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

@Component
public class PaymentEventsListener {

    private static final Logger log = LoggerFactory.getLogger(PaymentEventsListener.class);

    private final InventoryService inventoryService;
    private final IdempotencyService idempotencyService;

    public PaymentEventsListener(InventoryService inventoryService, IdempotencyService idempotencyService) {
        this.inventoryService = inventoryService;
        this.idempotencyService = idempotencyService;
    }

    @KafkaListener(topics = Topics.PAYMENT_FAILED)
    public void onPaymentFailed(
            PaymentFailedEvent event,
            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset) {

        String messageKey = topic + "-" + partition + "-" + offset;
        if (!idempotencyService.markProcessed(messageKey)) {
            log.info("Duplicate delivery of {} ignored (orderId={})", messageKey, event.orderId());
            return;
        }

        log.info("Received payment.failed orderId={} reason={}", event.orderId(), event.reason());

        ReleaseResult result = inventoryService.release(event.orderId());

        switch (result.outcome()) {
            case RELEASED -> log.info("Released stock reservation for order {}", event.orderId());
            case ALREADY_RELEASED -> log.info("Reservation for order {} already released, ignoring", event.orderId());
            case NOT_FOUND -> log.warn("No reservation found for order {}, nothing to release", event.orderId());
        }
    }
}
