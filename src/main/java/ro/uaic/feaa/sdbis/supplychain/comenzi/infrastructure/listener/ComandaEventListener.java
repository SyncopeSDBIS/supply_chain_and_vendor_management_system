package ro.uaic.feaa.sdbis.supplychain.comenzi.infrastructure.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import ro.uaic.feaa.sdbis.supplychain.calitate.api.event.VerdictInspectiePublicEvent;
import ro.uaic.feaa.sdbis.supplychain.comenzi.application.ComandaApplicationService;
import ro.uaic.feaa.sdbis.supplychain.comenzi.domain.RezultatCalitateLot;
import ro.uaic.feaa.sdbis.supplychain.shared.InboxRepo;

/**
 * Event Listener asincron pentru Bounded Context Comenzi (Figura 1 Context Map).
 * Actioneaza ca Anti-Corruption Layer (ACL) primind VerdictInspectiePublicEvent din Calitate
 * si aplicand tranzitia de stare asupra lotului (Eliberat in stoc / Respins).
 */
@Component
public class ComandaEventListener {

    private static final Logger log = LoggerFactory.getLogger(ComandaEventListener.class);
    private static final String CONSUMER_ID = "ComandaEventListener";

    private final ComandaApplicationService comandaAppService;
    private final InboxRepo inboxRepo;

    public ComandaEventListener(ComandaApplicationService comandaAppService, InboxRepo inboxRepo) {
        this.comandaAppService = comandaAppService;
        this.inboxRepo = inboxRepo;
    }

    @EventListener
    public void onVerdictInspectie(VerdictInspectiePublicEvent event) {
        if (inboxRepo.existaEveniment(CONSUMER_ID, event.eventId())) {
            log.info("[ComandaEventListener] Event {} already processed, skipping (Idempotent Inbox).", event.eventId());
            return;
        }

        try {
            RezultatCalitateLot rezultat = event.conforma()
                ? RezultatCalitateLot.Conform
                : RezultatCalitateLot.Neconform;

            comandaAppService.aplicaVerdictInspectie(event.comandaId(), event.lotId(), rezultat);
            inboxRepo.marcheazaProcesat(CONSUMER_ID, event.eventId());
            log.info("[ComandaEventListener] Applied quality inspection verdict {} on lot {} of order {}",
                rezultat, event.lotId().getValue(), event.comandaId().getValue());
        } catch (Exception ex) {
            log.error("[ComandaEventListener] Error applying inspection verdict for event {}: {}", event.eventId(), ex.getMessage(), ex);
        }
    }
}
