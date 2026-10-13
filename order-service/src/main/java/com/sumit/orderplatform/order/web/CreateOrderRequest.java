package com.sumit.orderplatform.order.web;

import com.sumit.orderplatform.events.PaymentMethod;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record CreateOrderRequest(
        @NotBlank(message = "customerId is required")
        String customerId,

        @NotBlank(message = "productId is required")
        String productId,

        @Min(value = 1, message = "quantity must be at least 1")
        int quantity,

        @NotNull(message = "amount is required")
        @DecimalMin(value = "0.01", message = "amount must be greater than 0")
        BigDecimal amount,

        PaymentMethod paymentMethod) {
}
