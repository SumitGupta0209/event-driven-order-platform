package com.sumit.orderplatform.payment.strategy;

import com.sumit.orderplatform.payment.domain.Account;
import com.sumit.orderplatform.payment.repository.AccountRepository;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class WalletPaymentStrategy implements PaymentStrategy {

    private static final BigDecimal WALLET_LIMIT = new BigDecimal("500.00");

    private final AccountRepository accountRepository;

    public WalletPaymentStrategy(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Override
    public PaymentOutcome charge(Account account, BigDecimal amount) {
        if (amount.compareTo(WALLET_LIMIT) > 0) {
            return PaymentOutcome.failure(
                    "Wallet payments are limited to " + WALLET_LIMIT + " per transaction");
        }

        int debited = accountRepository.debit(account.getCustomerId(), amount);
        if (debited == 0) {
            return PaymentOutcome.failure("Insufficient funds for wallet payment");
        }
        return PaymentOutcome.success(amount);
    }
}
