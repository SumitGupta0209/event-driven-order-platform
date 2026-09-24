package com.sumit.orderplatform.order.domain;

class PendingState implements OrderState {

    @Override
    public OrderStatus onInventoryReserved() {
        return OrderStatus.INVENTORY_RESERVED;
    }

    @Override
    public OrderStatus onInventoryFailed() {
        return OrderStatus.CANCELLED;
    }

    @Override
    public OrderStatus onPaymentCompleted() {
        return OrderStatus.CONFIRMED;
    }

    @Override
    public OrderStatus onPaymentFailed() {
        return OrderStatus.CANCELLED;
    }
}
