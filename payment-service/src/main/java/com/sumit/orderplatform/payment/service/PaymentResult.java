package com.sumit.orderplatform.payment.service;

import java.util.UUID;

public record PaymentResult(Outcome outcome, UUID paymentId, String reason) {

    public enum Outcome {
        COMPLETED,
        FAILED,
        DUPLICATE
    }

    public static PaymentResult completed(UUID paymentId) {
        return new PaymentResult(Outcome.COMPLETED, paymentId, null);
    }

    public static PaymentResult failed(UUID paymentId, String reason) {
        return new PaymentResult(Outcome.FAILED, paymentId, reason);
    }

    public static PaymentResult duplicate(UUID paymentId) {
        return new PaymentResult(Outcome.DUPLICATE, paymentId, null);
    }
}
