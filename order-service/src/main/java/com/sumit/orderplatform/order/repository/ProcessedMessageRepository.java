package com.sumit.orderplatform.order.repository;

import com.sumit.orderplatform.order.domain.ProcessedMessage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProcessedMessageRepository extends JpaRepository<ProcessedMessage, String> {
}
