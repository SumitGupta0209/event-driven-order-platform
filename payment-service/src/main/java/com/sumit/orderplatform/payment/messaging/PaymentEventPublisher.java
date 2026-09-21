package com.sumit.orderplatform.payment.messaging;

import com.sumit.orderplatform.events.PaymentCompletedEvent;
import com.sumit.orderplatform.events.PaymentFailedEvent;
import com.sumit.orderplatform.events.Topics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Component
public class PaymentEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(PaymentEventPublisher.class);

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public PaymentEventPublisher(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publishCompleted(UUID paymentId, UUID orderId, BigDecimal amount) {
        PaymentCompletedEvent event = new PaymentCompletedEvent(orderId, paymentId, amount, Instant.now());
        send(Topics.PAYMENT_COMPLETED, orderId, event);
    }

    public void publishFailed(UUID orderId, String reason) {
        PaymentFailedEvent event = new PaymentFailedEvent(orderId, reason, Instant.now());
        send(Topics.PAYMENT_FAILED, orderId, event);
    }

    private void send(String topic, UUID orderId, Object event) {
        String key = orderId.toString();
        kafkaTemplate.send(topic, key, event)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("Failed to publish {} for order {}", topic, key, ex);
                    } else {
                        log.info("Published {} key={} partition={} offset={}",
                                topic,
                                key,
                                result.getRecordMetadata().partition(),
                                result.getRecordMetadata().offset());
                    }
                });
    }
}
