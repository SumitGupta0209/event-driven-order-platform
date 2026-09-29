package com.sumit.orderplatform.payment.strategy;

import com.sumit.orderplatform.events.PaymentMethod;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class PaymentStrategyFactory {

    private final Map<PaymentMethod, PaymentStrategy> strategies;

    public PaymentStrategyFactory(CardPaymentStrategy cardPaymentStrategy,
                                  WalletPaymentStrategy walletPaymentStrategy) {
        this.strategies = Map.of(
                PaymentMethod.CARD, cardPaymentStrategy,
                PaymentMethod.WALLET, walletPaymentStrategy);
    }

    public PaymentStrategy get(PaymentMethod method) {
        PaymentStrategy strategy = strategies.get(method);
        if (strategy == null) {
            throw new IllegalArgumentException("No strategy for payment method " + method);
        }
        return strategy;
    }
}
