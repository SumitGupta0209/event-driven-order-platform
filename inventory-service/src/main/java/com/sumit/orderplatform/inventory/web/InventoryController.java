package com.sumit.orderplatform.inventory.web;

import com.sumit.orderplatform.inventory.domain.InventoryItem;
import com.sumit.orderplatform.inventory.repository.InventoryItemRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/inventory")
public class InventoryController {

    private final InventoryItemRepository repository;

    public InventoryController(InventoryItemRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/{productId}/stock")
    public StockResponse getStock(@PathVariable String productId) {
        InventoryItem item = repository.findById(productId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Unknown product " + productId));
        return new StockResponse(item.getProductId(), item.getAvailableQuantity());
    }
}
