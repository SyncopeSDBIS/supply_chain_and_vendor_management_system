package ro.uaic.feaa.sdbis.supplychain.infrastructure.messaging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ro.uaic.feaa.sdbis.supplychain.shared.DistributedLockService;
import ro.uaic.feaa.sdbis.supplychain.shared.DomainEvent;
import ro.uaic.feaa.sdbis.supplychain.shared.OutboxRepo;
import ro.uaic.feaa.sdbis.supplychain.shared.annotation.InfrastructureService;
import ro.uaic.feaa.sdbis.supplychain.shared.messaging.AmqpEventPublisher;

import java.time.Duration;
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
    private final AmqpEventPublisher amqpPublisher;
    private final DistributedLockService distributedLockService;

    public OutboxDispatcherService(OutboxRepo outboxRepo,
                                   ApplicationEventPublisher eventPublisher,
                                   AmqpEventPublisher amqpPublisher,
                                   @Autowired(required = false) DistributedLockService distributedLockService) {
        this.outboxRepo = outboxRepo;
        this.eventPublisher = eventPublisher;
        this.amqpPublisher = amqpPublisher;
        this.distributedLockService = distributedLockService;
    }

    @org.springframework.beans.factory.annotation.Value("${supplychain.outbox.dispatcher.enabled:false}")
    private boolean dispatcherEnabled = false;

    @Scheduled(fixedDelay = 2000)
    public int scheduledDispatch() {
        if (!dispatcherEnabled) {
            return 0;
        }
        return dispatchUnpublishedEvents();
    }

    public int dispatchUnpublishedEvents() {
        if (distributedLockService != null && !distributedLockService.tryLock("outboxDispatcher", Duration.ofSeconds(30))) {
            return 0;
        }
        try {
            List<DomainEvent> events = outboxRepo.pollUnpublishedEvents();
            int count = 0;
            for (DomainEvent e : events) {
                try {
                    amqpPublisher.publishDomainEvent(e);
                    outboxRepo.markAsPublished(e.eventId());
                    count++;
                    log.info("[Outbox Dispatcher] Published event {} of type {}", e.eventId(), e.getClass().getSimpleName());
                } catch (Exception ex) {
                    log.warn("[Outbox Dispatcher Transient Error] Failed to publish event {} ({}) - will retry next cycle (Inbox deduplication ensures idempotency): {}",
                            e.eventId(), e.getClass().getSimpleName(), ex.getMessage());
                }
            }
            return count;
        } finally {
            if (distributedLockService != null) {
                distributedLockService.releaseLock("outboxDispatcher");
            }
        }
    }
}
