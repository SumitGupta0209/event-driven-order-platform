package com.sumit.orderplatform.payment.domain;

import com.sumit.orderplatform.events.PaymentMethod;
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
@Table(name = "payments")
public class Payment {

    @Id
    private UUID paymentId;

    @Version
    private Long version;

    @Column(nullable = false, unique = true)
    private UUID orderId;

    @Column(nullable = false)
    private String customerId;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentMethod paymentMethod;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus status;

    private String failureReason;

    @Column(nullable = false)
    private Instant createdAt;

    protected Payment() {
    }

    private Payment(UUID orderId, String customerId, BigDecimal amount, PaymentMethod paymentMethod,
                    PaymentStatus status, String failureReason) {
        this.paymentId = UUID.randomUUID();
        this.orderId = orderId;
        this.customerId = customerId;
        this.amount = amount;
        this.paymentMethod = paymentMethod;
        this.status = status;
        this.failureReason = failureReason;
        this.createdAt = Instant.now();
    }

    public static Payment completed(UUID orderId, String customerId, BigDecimal amount, PaymentMethod paymentMethod) {
        return new Payment(orderId, customerId, amount, paymentMethod, PaymentStatus.COMPLETED, null);
    }

    public static Payment failed(UUID orderId, String customerId, BigDecimal amount, PaymentMethod paymentMethod, String reason) {
        return new Payment(orderId, customerId, amount, paymentMethod, PaymentStatus.FAILED, reason);
    }

    public UUID getPaymentId() {
        return paymentId;
    }

    public UUID getOrderId() {
        return orderId;
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public String getFailureReason() {
        return failureReason;
    }
}
