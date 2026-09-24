package com.sumit.orderplatform.order.service;

import com.sumit.orderplatform.order.domain.Order;

public record TransitionResult(Outcome outcome, Order order) {

    public enum Outcome {
        CHANGED,
        UNCHANGED,
        NOT_FOUND
    }

    public static TransitionResult changed(Order order) {
        return new TransitionResult(Outcome.CHANGED, order);
    }

    public static TransitionResult unchanged(Order order) {
        return new TransitionResult(Outcome.UNCHANGED, order);
    }

    public static TransitionResult notFound() {
        return new TransitionResult(Outcome.NOT_FOUND, null);
    }
}
