package com.sumit.orderplatform.order.domain;

class ConfirmedState implements OrderState {

    @Override
    public OrderStatus onInventoryReserved() {
        return OrderStatus.CONFIRMED;
    }

    @Override
    public OrderStatus onInventoryFailed() {
        return OrderStatus.CONFIRMED;
    }

    @Override
    public OrderStatus onPaymentCompleted() {
        return OrderStatus.CONFIRMED;
    }

    @Override
    public OrderStatus onPaymentFailed() {
        return OrderStatus.CONFIRMED;
    }
}
