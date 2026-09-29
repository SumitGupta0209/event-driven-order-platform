package com.sumit.orderplatform.order.web;

import com.sumit.orderplatform.events.PaymentMethod;

import java.math.BigDecimal;

public record CreateOrderRequest(
        String customerId,
        String productId,
        int quantity,
        BigDecimal amount,
        PaymentMethod paymentMethod) {
}
