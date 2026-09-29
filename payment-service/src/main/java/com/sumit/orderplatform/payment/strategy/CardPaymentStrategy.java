package com.sumit.orderplatform.payment.strategy;

import com.sumit.orderplatform.payment.domain.Account;
import com.sumit.orderplatform.payment.repository.AccountRepository;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Component
public class CardPaymentStrategy implements PaymentStrategy {

    private static final BigDecimal FEE_RATE = new BigDecimal("0.02");

    private final AccountRepository accountRepository;

    public CardPaymentStrategy(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Override
    public PaymentOutcome charge(Account account, BigDecimal amount) {
        BigDecimal fee = amount.multiply(FEE_RATE).setScale(2, RoundingMode.HALF_UP);
        BigDecimal total = amount.add(fee);

        int debited = accountRepository.debit(account.getCustomerId(), total);
        if (debited == 0) {
            return PaymentOutcome.failure(
                    "Insufficient funds for card payment (amount " + amount + " + fee " + fee + ")");
        }
        return PaymentOutcome.success(total);
    }
}
