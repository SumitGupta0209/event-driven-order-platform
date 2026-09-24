package com.sumit.orderplatform.order.domain;

class CancelledState implements OrderState {

    @Override
    public OrderStatus onInventoryReserved() {
        return OrderStatus.CANCELLED;
    }

    @Override
    public OrderStatus onInventoryFailed() {
        return OrderStatus.CANCELLED;
    }

    @Override
    public OrderStatus onPaymentCompleted() {
        return OrderStatus.CANCELLED;
    }

    @Override
    public OrderStatus onPaymentFailed() {
        return OrderStatus.CANCELLED;
    }
}
