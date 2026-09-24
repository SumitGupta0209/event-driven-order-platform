package com.sumit.orderplatform.order.domain;

final class OrderStates {

    private static final OrderState PENDING = new PendingState();
    private static final OrderState INVENTORY_RESERVED = new InventoryReservedState();
    private static final OrderState CONFIRMED = new ConfirmedState();
    private static final OrderState CANCELLED = new CancelledState();

    private OrderStates() {
    }

    static OrderState of(OrderStatus status) {
        return switch (status) {
            case PENDING -> PENDING;
            case INVENTORY_RESERVED -> INVENTORY_RESERVED;
            case CONFIRMED -> CONFIRMED;
            case CANCELLED -> CANCELLED;
        };
    }
}
