package com.sumit.orderplatform.payment.strategy;

import java.math.BigDecimal;

public record PaymentOutcome(boolean success, BigDecimal totalCharged, String failureReason) {

    public static PaymentOutcome success(BigDecimal totalCharged) {
        return new PaymentOutcome(true, totalCharged, null);
    }

    public static PaymentOutcome failure(String reason) {
        return new PaymentOutcome(false, null, reason);
    }
}
