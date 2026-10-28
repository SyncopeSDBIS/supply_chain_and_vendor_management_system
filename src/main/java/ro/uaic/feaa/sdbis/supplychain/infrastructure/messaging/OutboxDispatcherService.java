package ro.uaic.feaa.sdbis.supplychain.infrastructure.messaging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ro.uaic.feaa.sdbis.supplychain.shared.DomainEvent;
import ro.uaic.feaa.sdbis.supplychain.shared.OutboxRepo;
import ro.uaic.feaa.sdbis.supplychain.shared.annotation.InfrastructureService;

import java.util.List;

/**
 * Dispatcher Transactional Outbox (ADR-002 si Context Map Figura 1).
 * Polleaza periodic evenimentele nepublicate si le transmite catre consumatorii asincroni.
 */
@Service
@InfrastructureService
public class OutboxDispatcherService {

    private static final Logger log = LoggerFactory.getLogger(OutboxDispatcherService.class);

    private final OutboxRepo outboxRepo;
    private final ApplicationEventPublisher eventPublisher;
    private final ro.uaic.feaa.sdbis.supplychain.shared.messaging.AmqpEventPublisher amqpPublisher;

    public OutboxDispatcherService(OutboxRepo outboxRepo,
                                  ApplicationEventPublisher eventPublisher,
                                  ro.uaic.feaa.sdbis.supplychain.shared.messaging.AmqpEventPublisher amqpPublisher) {
        this.outboxRepo = outboxRepo;
        this.eventPublisher = eventPublisher;
        this.amqpPublisher = amqpPublisher;
    }

    @Scheduled(fixedDelay = 2000)
    @Transactional
    public int dispatchUnpublishedEvents() {
        List<DomainEvent> events = outboxRepo.pollUnpublishedEvents();
        int count = 0;
        for (DomainEvent e : events) {
            try {
                eventPublisher.publishEvent(e);
                amqpPublisher.publishDomainEvent(e);
                outboxRepo.markAsPublished(e.eventId());
                count++;
                log.info("[Outbox Dispatcher] Published event {} of type {}", e.eventId(), e.getClass().getSimpleName());
            } catch (Exception ex) {
                log.error("[Outbox Dispatcher] Failed to publish event {}: {}", e.eventId(), ex.getMessage(), ex);
            }
        }
        return count;
    }
}
