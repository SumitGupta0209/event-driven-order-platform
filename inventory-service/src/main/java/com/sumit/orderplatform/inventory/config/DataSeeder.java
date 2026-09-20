package com.sumit.orderplatform.inventory.config;

import com.sumit.orderplatform.inventory.domain.InventoryItem;
import com.sumit.orderplatform.inventory.repository.InventoryItemRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataSeeder {

    @Bean
    public CommandLineRunner seedInventory(InventoryItemRepository repository) {
        return args -> {
            if (repository.count() == 0) {
                repository.save(new InventoryItem("P1", 100));
                repository.save(new InventoryItem("P2", 5));
                repository.save(new InventoryItem("P3", 0));
            }
        };
    }
}
