package com.sumit.orderplatform.payment.repository;

import com.sumit.orderplatform.payment.domain.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;

public interface AccountRepository extends JpaRepository<Account, String> {

    @Modifying(clearAutomatically = true)
    @Query("update Account a "
            + "set a.balance = a.balance - :amount "
            + "where a.customerId = :customerId and a.balance >= :amount")
    int debit(@Param("customerId") String customerId, @Param("amount") BigDecimal amount);
}
