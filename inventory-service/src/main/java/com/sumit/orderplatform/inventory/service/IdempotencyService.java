package com.sumit.orderplatform.inventory.service;

import com.sumit.orderplatform.inventory.domain.ProcessedMessage;
import com.sumit.orderplatform.inventory.repository.ProcessedMessageRepository;
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

    /**
     * Public entry point. Deliberately NOT @Transactional itself, so that
     * a failure inside insertNew()'s own transaction can be caught safely
     * here, after that transaction has already rolled back on its own.
     * Catching a DataAccessException inside the same @Transactional method
     * that threw it does not work reliably: Spring may have already marked
     * the transaction rollback-only, causing UnexpectedRollbackException
     * on commit even though the original exception was caught.
     */
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
