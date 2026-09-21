package com.sumit.orderplatform.payment.config;

import com.sumit.orderplatform.payment.domain.Account;
import com.sumit.orderplatform.payment.repository.AccountRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;

@Configuration
public class DataSeeder {

    @Bean
    public CommandLineRunner seedAccounts(AccountRepository repository) {
        return args -> {
            if (repository.count() == 0) {
                repository.save(new Account("C1", new BigDecimal("1000.00")));
                repository.save(new Account("C2", new BigDecimal("1000.00")));
                repository.save(new Account("LOW", new BigDecimal("10.00")));
            }
        };
    }
}
