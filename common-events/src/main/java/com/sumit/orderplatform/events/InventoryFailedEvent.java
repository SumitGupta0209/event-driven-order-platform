package com.sumit.orderplatform.events;

import java.time.Instant;
import java.util.UUID;

public record InventoryFailedEvent(
        UUID orderId,
        String reason,
        Instant failedAt) {
}
