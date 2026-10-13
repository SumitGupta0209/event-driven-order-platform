package com.sumit.orderplatform.order.outbox;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
public class OutboxPoller {

    private static final Logger log = LoggerFactory.getLogger(OutboxPoller.class);

    private final OutboxEventRepository repository;
    private final KafkaTemplate<String, String> kafkaTemplate;

    public OutboxPoller(OutboxEventRepository repository, KafkaTemplate<String, String> kafkaTemplate) {
        this.repository = repository;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Scheduled(fixedDelay = 2000)
    @Transactional
    public void publishPending() {
        List<OutboxEvent> pending = repository.findTop50ByPublishedFalseOrderByCreatedAtAsc();
        if (pending.isEmpty()) {
            return;
        }

        log.info("Outbox poller: publishing {} pending event(s)", pending.size());

        for (OutboxEvent event : pending) {
            try {
                kafkaTemplate.send(event.getTopic(), event.getAggregateId(), event.getPayload()).get();
                event.markPublished();
                log.info("Outbox: published {} for aggregate {}", event.getTopic(), event.getAggregateId());
            } catch (Exception e) {
                log.error("Outbox: failed to publish {} for aggregate {}, will retry next poll",
                        event.getTopic(), event.getAggregateId(), e);
                // Don't rethrow: leave this row unpublished and let the next
                // scheduled run retry it. Other rows in this batch still proceed.
            }
        }
    }
}
