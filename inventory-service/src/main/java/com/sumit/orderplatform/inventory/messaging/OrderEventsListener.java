package com.sumit.orderplatform.inventory.messaging;

import com.sumit.orderplatform.events.OrderCreatedEvent;
import com.sumit.orderplatform.events.Topics;
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

    public OrderEventsListener(InventoryService inventoryService, InventoryEventPublisher publisher) {
        this.inventoryService = inventoryService;
        this.publisher = publisher;
    }

    @KafkaListener(topics = Topics.ORDER_CREATED)
    public void onOrderCreated(
            OrderCreatedEvent event,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset) {

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
