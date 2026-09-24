package com.sumit.orderplatform.inventory.messaging;

import com.sumit.orderplatform.events.PaymentFailedEvent;
import com.sumit.orderplatform.events.Topics;
import com.sumit.orderplatform.inventory.service.InventoryService;
import com.sumit.orderplatform.inventory.service.ReleaseResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class PaymentEventsListener {

    private static final Logger log = LoggerFactory.getLogger(PaymentEventsListener.class);

    private final InventoryService inventoryService;

    public PaymentEventsListener(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @KafkaListener(topics = Topics.PAYMENT_FAILED)
    public void onPaymentFailed(PaymentFailedEvent event) {
        log.info("Received payment.failed orderId={} reason={}", event.orderId(), event.reason());

        ReleaseResult result = inventoryService.release(event.orderId());

        switch (result.outcome()) {
            case RELEASED -> log.info("Released stock reservation for order {}", event.orderId());
            case ALREADY_RELEASED -> log.info("Reservation for order {} already released, ignoring", event.orderId());
            case NOT_FOUND -> log.warn("No reservation found for order {}, nothing to release", event.orderId());
        }
    }
}
