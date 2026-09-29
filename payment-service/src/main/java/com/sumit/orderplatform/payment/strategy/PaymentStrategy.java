package com.sumit.orderplatform.payment.strategy;

import com.sumit.orderplatform.payment.domain.Account;

import java.math.BigDecimal;

public interface PaymentStrategy {

    PaymentOutcome charge(Account account, BigDecimal amount);
}
