package com.sumit.orderplatform.payment.repository;

import com.sumit.orderplatform.payment.domain.ProcessedMessage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProcessedMessageRepository extends JpaRepository<ProcessedMessage, String> {
}
