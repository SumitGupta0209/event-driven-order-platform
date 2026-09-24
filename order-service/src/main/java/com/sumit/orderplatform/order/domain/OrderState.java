package com.sumit.orderplatform.order.domain;

interface OrderState {

    OrderStatus onInventoryReserved();

    OrderStatus onInventoryFailed();

    OrderStatus onPaymentCompleted();

    OrderStatus onPaymentFailed();
}
