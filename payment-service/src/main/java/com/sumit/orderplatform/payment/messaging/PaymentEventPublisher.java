package com.sumit.orderplatform.payment.messaging;

import com.sumit.orderplatform.events.PaymentCompletedEvent;
import com.sumit.orderplatform.events.PaymentFailedEvent;
import com.sumit.orderplatform.events.Topics;
import com.sumit.orderplatform.payment.outbox.OutboxWriter;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Component
public class PaymentEventPublisher {

    private final OutboxWriter outboxWriter;

    public PaymentEventPublisher(OutboxWriter outboxWriter) {
        this.outboxWriter = outboxWriter;
    }

    public void publishCompleted(UUID paymentId, UUID orderId, BigDecimal amount) {
        PaymentCompletedEvent event = new PaymentCompletedEvent(orderId, paymentId, amount, Instant.now());
        outboxWriter.write(orderId.toString(), Topics.PAYMENT_COMPLETED, event);
    }

    public void publishFailed(UUID orderId, String reason) {
        PaymentFailedEvent event = new PaymentFailedEvent(orderId, reason, Instant.now());
        outboxWriter.write(orderId.toString(), Topics.PAYMENT_FAILED, event);
    }
}
