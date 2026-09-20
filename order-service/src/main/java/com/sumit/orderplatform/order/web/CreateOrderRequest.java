package com.sumit.orderplatform.order.web;

import java.math.BigDecimal;

public record CreateOrderRequest(
        String customerId,
        String productId,
        int quantity,
        BigDecimal amount) {
}
