package com.sumit.orderplatform.order.web;

import com.sumit.orderplatform.order.client.InventoryStockResponse;
import com.sumit.orderplatform.order.client.InventoryStockService;
import com.sumit.orderplatform.order.domain.Order;
import com.sumit.orderplatform.order.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;
    private final InventoryStockService inventoryStockService;

    public OrderController(OrderService orderService, InventoryStockService inventoryStockService) {
        this.orderService = orderService;
        this.inventoryStockService = inventoryStockService;
    }

    @PostMapping
    public ResponseEntity<OrderResponse> create(@Valid @RequestBody CreateOrderRequest request) {
        Order order = orderService.createOrder(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(OrderResponse.from(order));
    }

    @GetMapping("/{id}")
    public OrderResponse get(@PathVariable UUID id) {
        return orderService.findById(id)
                .map(OrderResponse::from)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found"));
    }

    @GetMapping("/check-stock/{productId}")
    public InventoryStockResponse checkStock(@PathVariable String productId) {
        return inventoryStockService.checkStock(productId);
    }
}
