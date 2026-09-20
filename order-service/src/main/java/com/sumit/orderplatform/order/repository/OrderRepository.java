package com.sumit.orderplatform.order.repository;

import com.sumit.orderplatform.order.domain.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface OrderRepository extends JpaRepository<Order, UUID> {
}
