package com.sumit.orderplatform.payment.service;

import com.sumit.orderplatform.events.InventoryReservedEvent;
import com.sumit.orderplatform.payment.domain.Account;
import com.sumit.orderplatform.payment.domain.Payment;
import com.sumit.orderplatform.payment.repository.AccountRepository;
import com.sumit.orderplatform.payment.repository.PaymentRepository;
import com.sumit.orderplatform.payment.strategy.PaymentOutcome;
import com.sumit.orderplatform.payment.strategy.PaymentStrategy;
import com.sumit.orderplatform.payment.strategy.PaymentStrategyFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class PaymentService {

    private final AccountRepository accountRepository;
    private final PaymentRepository paymentRepository;
    private final PaymentStrategyFactory strategyFactory;

    public PaymentService(AccountRepository accountRepository,
                          PaymentRepository paymentRepository,
                          PaymentStrategyFactory strategyFactory) {
        this.accountRepository = accountRepository;
        this.paymentRepository = paymentRepository;
        this.strategyFactory = strategyFactory;
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

        Optional<Account> account = accountRepository.findById(event.customerId());
        if (account.isEmpty()) {
            return recordFailure(event, "Unknown customer " + event.customerId());
        }

        PaymentStrategy strategy = strategyFactory.get(event.paymentMethod());
        PaymentOutcome outcome = strategy.charge(account.get(), event.amount());

        if (!outcome.success()) {
            return recordFailure(event, outcome.failureReason());
        }

        Payment payment = paymentRepository.save(
                Payment.completed(event.orderId(), event.customerId(), event.amount(), event.paymentMethod()));
        return PaymentResult.completed(payment.getPaymentId());
    }

    private PaymentResult recordFailure(InventoryReservedEvent event, String reason) {
        Payment payment = paymentRepository.save(
                Payment.failed(event.orderId(), event.customerId(), event.amount(), event.paymentMethod(), reason));
        return PaymentResult.failed(payment.getPaymentId(), reason);
    }
}
