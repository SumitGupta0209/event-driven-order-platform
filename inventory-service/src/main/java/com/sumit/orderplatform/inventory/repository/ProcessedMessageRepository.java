package com.sumit.orderplatform.inventory.repository;

import com.sumit.orderplatform.inventory.domain.ProcessedMessage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProcessedMessageRepository extends JpaRepository<ProcessedMessage, String> {
}
