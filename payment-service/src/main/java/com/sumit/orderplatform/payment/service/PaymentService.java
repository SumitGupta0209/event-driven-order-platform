package com.sumit.orderplatform.payment.service;

import com.sumit.orderplatform.events.InventoryReservedEvent;
import com.sumit.orderplatform.payment.domain.Payment;
import com.sumit.orderplatform.payment.repository.AccountRepository;
import com.sumit.orderplatform.payment.repository.PaymentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class PaymentService {

    private final AccountRepository accountRepository;
    private final PaymentRepository paymentRepository;

    public PaymentService(AccountRepository accountRepository, PaymentRepository paymentRepository) {
        this.accountRepository = accountRepository;
        this.paymentRepository = paymentRepository;
    }

    @Transactional
    public PaymentResult process(InventoryReservedEvent event) {
        Optional<Payment> existing = paymentRepository.findByOrderId(event.orderId());
        if (existing.isPresent()) {
            return PaymentResult.duplicate(existing.get().getPaymentId());
        }

        if (event.amount() == null || event.amount().signum() <= 0) {
            return recordFailure(event, "Invalid amount " + event.amount());
        }

        int debited = accountRepository.debit(event.customerId(), event.amount());
        if (debited == 0) {
            String reason = accountRepository.existsById(event.customerId())
                    ? "Insufficient funds for customer " + event.customerId()
                    : "Unknown customer " + event.customerId();
            return recordFailure(event, reason);
        }

        Payment payment = paymentRepository.save(
                Payment.completed(event.orderId(), event.customerId(), event.amount()));
        return PaymentResult.completed(payment.getPaymentId());
    }

    private PaymentResult recordFailure(InventoryReservedEvent event, String reason) {
        Payment payment = paymentRepository.save(
                Payment.failed(event.orderId(), event.customerId(), event.amount(), reason));
        return PaymentResult.failed(payment.getPaymentId(), reason);
    }
}
