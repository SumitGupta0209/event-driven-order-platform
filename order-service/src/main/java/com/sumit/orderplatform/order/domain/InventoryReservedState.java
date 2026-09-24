package com.sumit.orderplatform.order.domain;

class InventoryReservedState implements OrderState {

    @Override
    public OrderStatus onInventoryReserved() {
        return OrderStatus.INVENTORY_RESERVED;
    }

    @Override
    public OrderStatus onInventoryFailed() {
        return OrderStatus.INVENTORY_RESERVED;
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
