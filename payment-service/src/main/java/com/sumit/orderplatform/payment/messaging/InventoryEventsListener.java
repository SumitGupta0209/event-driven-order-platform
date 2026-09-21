package com.sumit.orderplatform.payment.messaging;

import com.sumit.orderplatform.events.InventoryReservedEvent;
import com.sumit.orderplatform.events.Topics;
import com.sumit.orderplatform.payment.service.PaymentResult;
import com.sumit.orderplatform.payment.service.PaymentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

@Component
public class InventoryEventsListener {

    private static final Logger log = LoggerFactory.getLogger(InventoryEventsListener.class);

    private final PaymentService paymentService;
    private final PaymentEventPublisher publisher;

    public InventoryEventsListener(PaymentService paymentService, PaymentEventPublisher publisher) {
        this.paymentService = paymentService;
        this.publisher = publisher;
    }

    @KafkaListener(topics = Topics.INVENTORY_RESERVED)
    public void onInventoryReserved(
            InventoryReservedEvent event,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset) {

        log.info("Received inventory.reserved orderId={} customer={} amount={} partition={} offset={}",
                event.orderId(), event.customerId(), event.amount(), partition, offset);

        PaymentResult result = paymentService.process(event);

        switch (result.outcome()) {
            case COMPLETED -> publisher.publishCompleted(result.paymentId(), event.orderId(), event.amount());
            case FAILED -> publisher.publishFailed(event.orderId(), result.reason());
            case DUPLICATE -> log.info("Duplicate inventory.reserved for {} ignored", event.orderId());
        }
    }
}
