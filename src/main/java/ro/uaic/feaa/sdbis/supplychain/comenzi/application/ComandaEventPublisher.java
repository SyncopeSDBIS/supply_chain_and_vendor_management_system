package ro.uaic.feaa.sdbis.supplychain.comenzi.application;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import ro.uaic.feaa.sdbis.supplychain.shared.DomainEvent;
import ro.uaic.feaa.sdbis.supplychain.shared.OutboxRepo;

/**
 * Serviciu dedicat in Application Layer pentru salvarea in transactional outbox
 * si publicarea asincrona/in-process a evenimentelor publice si de domeniu.
 * Permite decuplarea logicii de orchestrare a comenzilor de detaliile outbox-ului.
 */
@Component
public class ComandaEventPublisher {

    private final OutboxRepo outboxRepo;
    private final ApplicationEventPublisher eventPublisher;

    public ComandaEventPublisher(OutboxRepo outboxRepo,
                                 @Autowired(required = false) ApplicationEventPublisher eventPublisher) {
        this.outboxRepo = outboxRepo;
        this.eventPublisher = eventPublisher;
    }

    public void publishEvent(DomainEvent event) {
        if (event == null) return;
        outboxRepo.saveEvent(event);
        if (eventPublisher != null) {
            eventPublisher.publishEvent(event);
        }
    }

    public void saveInternalEvent(DomainEvent event) {
        if (event == null) return;
        outboxRepo.saveEvent(event);
    }
}

