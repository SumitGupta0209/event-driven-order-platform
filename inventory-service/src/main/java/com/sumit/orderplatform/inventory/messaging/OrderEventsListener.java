package com.sumit.orderplatform.inventory.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sumit.orderplatform.events.OrderCreatedEvent;
import com.sumit.orderplatform.events.Topics;
import com.sumit.orderplatform.inventory.service.IdempotencyService;
import com.sumit.orderplatform.inventory.service.InventoryService;
import com.sumit.orderplatform.inventory.service.ReservationResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

@Component
public class OrderEventsListener {

    private static final Logger log = LoggerFactory.getLogger(OrderEventsListener.class);

    private final InventoryService inventoryService;
    private final InventoryEventPublisher publisher;
    private final IdempotencyService idempotencyService;
    private final ObjectMapper objectMapper;

    public OrderEventsListener(InventoryService inventoryService,
                               InventoryEventPublisher publisher,
                               IdempotencyService idempotencyService,
                               ObjectMapper objectMapper) {
        this.inventoryService = inventoryService;
        this.publisher = publisher;
        this.idempotencyService = idempotencyService;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = Topics.ORDER_CREATED)
    public void onOrderCreated(
            String payload,
            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset) throws Exception {

        String messageKey = topic + "-" + partition + "-" + offset;
        if (!idempotencyService.markProcessed(messageKey)) {
            log.info("Duplicate delivery of {} ignored", messageKey);
            return;
        }

        OrderCreatedEvent event = objectMapper.readValue(payload, OrderCreatedEvent.class);

        log.info("Received order.created orderId={} product={} qty={} partition={} offset={}",
                event.orderId(), event.productId(), event.quantity(), partition, offset);

        ReservationResult result = inventoryService.reserve(event);

        switch (result.outcome()) {
            case RESERVED -> publisher.publishReserved(event);
            case REJECTED -> publisher.publishFailed(event.orderId(), result.reason());
            case DUPLICATE -> log.info("Duplicate order.created for {} ignored", event.orderId());
        }
    }
}
