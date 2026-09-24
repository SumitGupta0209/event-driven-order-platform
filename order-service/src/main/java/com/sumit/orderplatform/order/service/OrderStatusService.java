package com.sumit.orderplatform.order.service;

import com.sumit.orderplatform.order.domain.Order;
import com.sumit.orderplatform.order.repository.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;

@Service
public class OrderStatusService {

    private final OrderRepository orderRepository;

    public OrderStatusService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Transactional
    public TransitionResult inventoryReserved(UUID orderId) {
        return apply(orderId, Order::handleInventoryReserved);
    }

    @Transactional
    public TransitionResult inventoryFailed(UUID orderId, String reason) {
        return apply(orderId, order -> order.handleInventoryFailed(reason));
    }

    @Transactional
    public TransitionResult paymentCompleted(UUID orderId) {
        return apply(orderId, Order::handlePaymentCompleted);
    }

    @Transactional
    public TransitionResult paymentFailed(UUID orderId, String reason) {
        return apply(orderId, order -> order.handlePaymentFailed(reason));
    }

    private TransitionResult apply(UUID orderId, Predicate<Order> transition) {
        Optional<Order> found = orderRepository.findById(orderId);
        if (found.isEmpty()) {
            return TransitionResult.notFound();
        }
        Order order = found.get();
        boolean changed = transition.test(order);
        return changed ? TransitionResult.changed(order) : TransitionResult.unchanged(order);
    }
}
