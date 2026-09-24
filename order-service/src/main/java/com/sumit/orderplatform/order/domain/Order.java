package com.sumit.orderplatform.order.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "orders")
public class Order {

    @Id
    private UUID id;

    @Version
    private Long version;

    @Column(nullable = false)
    private String customerId;

    @Column(nullable = false)
    private String productId;

    @Column(nullable = false)
    private int quantity;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;

    private String cancellationReason;

    @Column(nullable = false)
    private Instant createdAt;

    protected Order() {
    }

    public Order(String customerId, String productId, int quantity, BigDecimal amount) {
        this.id = UUID.randomUUID();
        this.customerId = customerId;
        this.productId = productId;
        this.quantity = quantity;
        this.amount = amount;
        this.status = OrderStatus.PENDING;
        this.createdAt = Instant.now();
    }

    public boolean handleInventoryReserved() {
        return moveTo(state().onInventoryReserved(), null);
    }

    public boolean handleInventoryFailed(String reason) {
        return moveTo(state().onInventoryFailed(), reason);
    }

    public boolean handlePaymentCompleted() {
        return moveTo(state().onPaymentCompleted(), null);
    }

    public boolean handlePaymentFailed(String reason) {
        return moveTo(state().onPaymentFailed(), reason);
    }

    private OrderState state() {
        return OrderStates.of(status);
    }

    private boolean moveTo(OrderStatus next, String reason) {
        if (next == status) {
            return false;
        }
        this.status = next;
        if (next == OrderStatus.CANCELLED) {
            this.cancellationReason = reason;
        }
        return true;
    }

    public UUID getId() {
        return id;
    }

    public String getCustomerId() {
        return customerId;
    }

    public String getProductId() {
        return productId;
    }

    public int getQuantity() {
        return quantity;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public String getCancellationReason() {
        return cancellationReason;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
