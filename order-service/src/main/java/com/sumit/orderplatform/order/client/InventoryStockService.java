package com.sumit.orderplatform.order.client;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class InventoryStockService {

    private static final Logger log = LoggerFactory.getLogger(InventoryStockService.class);

    private final InventoryClient inventoryClient;

    public InventoryStockService(InventoryClient inventoryClient) {
        this.inventoryClient = inventoryClient;
    }

    @CircuitBreaker(name = "inventoryService", fallbackMethod = "stockFallback")
    public InventoryStockResponse checkStock(String productId) {
        return inventoryClient.getStock(productId);
    }

    private InventoryStockResponse stockFallback(String productId, Throwable throwable) {
        log.warn("Falling back for product {} due to: {}", productId, throwable.toString());
        return new InventoryStockResponse(productId, -1);
    }
}
