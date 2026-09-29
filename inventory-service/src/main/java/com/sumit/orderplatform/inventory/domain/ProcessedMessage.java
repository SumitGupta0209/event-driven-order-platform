package com.sumit.orderplatform.inventory.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import org.springframework.data.domain.Persistable;

import java.time.Instant;

@Entity
@Table(name = "processed_messages")
public class ProcessedMessage implements Persistable<String> {

    @Id
    private String messageKey;

    @Column(nullable = false)
    private Instant processedAt;

    @Transient
    private boolean isNew = true;

    protected ProcessedMessage() {
    }

    public ProcessedMessage(String messageKey) {
        this.messageKey = messageKey;
        this.processedAt = Instant.now();
        this.isNew = true;
    }

    @Override
    public String getId() {
        return messageKey;
    }

    @Override
    public boolean isNew() {
        return isNew;
    }

    public String getMessageKey() {
        return messageKey;
    }
}
