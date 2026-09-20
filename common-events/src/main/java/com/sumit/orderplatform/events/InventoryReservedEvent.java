package com.sumit.orderplatform.events;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record InventoryReservedEvent(
        UUID orderId,
        String customerId,
        String productId,
        int quantity,
        BigDecimal amount,
        Instant reservedAt) {
}
