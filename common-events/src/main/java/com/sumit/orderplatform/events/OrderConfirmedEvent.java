package com.sumit.orderplatform.events;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record OrderConfirmedEvent(
        UUID orderId,
        String customerId,
        BigDecimal amount,
        Instant confirmedAt) {
}
