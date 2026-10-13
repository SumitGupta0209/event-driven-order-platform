package com.sumit.orderplatform.order.outbox;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

@Component
public class OutboxWriter {

    private final OutboxEventRepository repository;
    private final ObjectMapper objectMapper;

    public OutboxWriter(OutboxEventRepository repository, ObjectMapper objectMapper) {
        this.repository = repository;
        this.objectMapper = objectMapper;
    }

    public void write(String aggregateId, String topic, Object event) {
        try {
            String json = objectMapper.writeValueAsString(event);
            repository.save(new OutboxEvent(aggregateId, topic, json));
        } catch (Exception e) {
            throw new IllegalStateException("Failed to serialize event for outbox", e);
        }
    }
}
