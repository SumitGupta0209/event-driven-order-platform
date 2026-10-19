package com.sumit.orderplatform.notification.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sumit.orderplatform.events.OrderCancelledEvent;
import com.sumit.orderplatform.events.OrderConfirmedEvent;
import com.sumit.orderplatform.events.Topics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class OrderEventsListener {

    private static final Logger log = LoggerFactory.getLogger(OrderEventsListener.class);

    private final ObjectMapper objectMapper;

    public OrderEventsListener(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = Topics.ORDER_CONFIRMED)
    public void onOrderConfirmed(String payload) throws Exception {
        OrderConfirmedEvent event = objectMapper.readValue(payload, OrderConfirmedEvent.class);
        log.info("NOTIFICATION -> customer {}: your order {} has been CONFIRMED. Amount charged: {}",
                event.customerId(), event.orderId(), event.amount());
    }

    @KafkaListener(topics = Topics.ORDER_CANCELLED)
    public void onOrderCancelled(String payload) throws Exception {
        OrderCancelledEvent event = objectMapper.readValue(payload, OrderCancelledEvent.class);
        log.info("NOTIFICATION -> customer {}: your order {} has been CANCELLED. Reason: {}",
                event.customerId(), event.orderId(), event.reason());
    }
}
