package com.sumit.orderplatform.order.service;

import com.sumit.orderplatform.order.domain.Order;
import com.sumit.orderplatform.order.messaging.OrderEventPublisher;
import com.sumit.orderplatform.order.repository.OrderRepository;
import com.sumit.orderplatform.order.web.CreateOrderRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderEventPublisher eventPublisher;

    public OrderService(OrderRepository orderRepository, OrderEventPublisher eventPublisher) {
        this.orderRepository = orderRepository;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public Order createOrder(CreateOrderRequest request) {
        Order order = new Order(
                request.customerId(),
                request.productId(),
                request.quantity(),
                request.amount(),
                request.paymentMethod());
        Order saved = orderRepository.save(order);
        eventPublisher.publishOrderCreated(saved);
        return saved;
    }

    @Transactional(readOnly = true)
    public Optional<Order> findById(UUID id) {
        return orderRepository.findById(id);
    }
}
