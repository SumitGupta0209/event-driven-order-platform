package com.sumit.orderplatform.payment.service;

import com.sumit.orderplatform.payment.domain.ProcessedMessage;
import com.sumit.orderplatform.payment.repository.ProcessedMessageRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class IdempotencyService {

    private final ProcessedMessageRepository repository;

    public IdempotencyService(ProcessedMessageRepository repository) {
        this.repository = repository;
    }

    public boolean markProcessed(String messageKey) {
        try {
            insertNew(messageKey);
            return true;
        } catch (DataIntegrityViolationException e) {
            return false;
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    protected void insertNew(String messageKey) {
        repository.save(new ProcessedMessage(messageKey));
        repository.flush();
    }
}
