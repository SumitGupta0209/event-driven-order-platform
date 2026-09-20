package com.sumit.orderplatform.inventory.repository;

import com.sumit.orderplatform.inventory.domain.StockReservation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface StockReservationRepository extends JpaRepository<StockReservation, UUID> {
}
