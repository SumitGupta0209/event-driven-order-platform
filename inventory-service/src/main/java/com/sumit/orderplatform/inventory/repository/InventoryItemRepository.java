package com.sumit.orderplatform.inventory.repository;

import com.sumit.orderplatform.inventory.domain.InventoryItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface InventoryItemRepository extends JpaRepository<InventoryItem, String> {

    @Modifying(clearAutomatically = true)
    @Query("update InventoryItem i "
            + "set i.availableQuantity = i.availableQuantity - :qty "
            + "where i.productId = :productId and i.availableQuantity >= :qty")
    int reserve(@Param("productId") String productId, @Param("qty") int qty);
}
