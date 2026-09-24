package com.sumit.orderplatform.inventory.service;

import com.sumit.orderplatform.events.OrderCreatedEvent;
import com.sumit.orderplatform.inventory.domain.StockReservation;
import com.sumit.orderplatform.inventory.repository.InventoryItemRepository;
import com.sumit.orderplatform.inventory.repository.StockReservationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
public class InventoryService {

    private final InventoryItemRepository itemRepository;
    private final StockReservationRepository reservationRepository;

    public InventoryService(InventoryItemRepository itemRepository,
                            StockReservationRepository reservationRepository) {
        this.itemRepository = itemRepository;
        this.reservationRepository = reservationRepository;
    }

    @Transactional
    public ReservationResult reserve(OrderCreatedEvent event) {
        if (reservationRepository.existsById(event.orderId())) {
            return ReservationResult.duplicate();
        }

        if (event.quantity() <= 0) {
            return ReservationResult.rejected("Invalid quantity " + event.quantity());
        }

        int updated = itemRepository.reserve(event.productId(), event.quantity());
        if (updated == 0) {
            String reason = itemRepository.existsById(event.productId())
                    ? "Insufficient stock for product " + event.productId()
                    : "Unknown product " + event.productId();
            return ReservationResult.rejected(reason);
        }

        reservationRepository.save(
                new StockReservation(event.orderId(), event.productId(), event.quantity()));
        return ReservationResult.reserved();
    }

    @Transactional
    public ReleaseResult release(UUID orderId) {
        Optional<StockReservation> found = reservationRepository.findById(orderId);
        if (found.isEmpty()) {
            return ReleaseResult.notFound();
        }

        StockReservation reservation = found.get();
        boolean released = reservation.release();
        if (!released) {
            return ReleaseResult.alreadyReleased();
        }

        itemRepository.release(reservation.getProductId(), reservation.getQuantity());
        return ReleaseResult.released();
    }
}
