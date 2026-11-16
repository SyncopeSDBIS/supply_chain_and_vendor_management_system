package ro.uaic.feaa.sdbis.supplychain.comenzi.infrastructure.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import ro.uaic.feaa.sdbis.supplychain.calitate.api.ICalitateModuleApi;
import ro.uaic.feaa.sdbis.supplychain.comenzi.domain.event.ComandaReceptionataEvent;
import ro.uaic.feaa.sdbis.supplychain.shared.InboxRepo;

import java.util.UUID;

/**
 * Event Listener asincron in Bounded Context Comenzi ce declanseaza programarea inspectiei AQL
 * prin ICalitateModuleApi la receptia marfii.
 */
@Component
public class ComandaReceptieEventListener {

    private static final Logger log = LoggerFactory.getLogger(ComandaReceptieEventListener.class);
    private static final String CONSUMER_ID = "ComandaReceptieEventListener";

    private final ICalitateModuleApi calitateApi;
    private final InboxRepo inboxRepo;

    public ComandaReceptieEventListener(ICalitateModuleApi calitateApi, InboxRepo inboxRepo) {
        this.calitateApi = calitateApi;
        this.inboxRepo = inboxRepo;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
    public void onComandaReceptionata(ComandaReceptionataEvent event) {
        if (!inboxRepo.incearcaInregistrare(CONSUMER_ID, event.eventId())) {
            log.info("[ComandaReceptieEventListener] Event {} already claimed/processed, skipping.", event.eventId());
            return;
        }

        try {
            UUID furnizorId = event.furnizorId() != null ? event.furnizorId().getValue() : null;
            UUID inspectieId = calitateApi.programeazaInspectie(
                event.lotId().getValue(),
                event.comandaId().getValue(),
                furnizorId,
                event.material().getCod()
            );

            log.info("[ComandaReceptieEventListener] Triggered inspection {} for lot {} from order {}",
                inspectieId, event.lotId().getValue(), event.comandaId().getValue());
        } catch (Exception ex) {
            log.error("[ComandaReceptieEventListener] Error triggering inspection for event {}: {}", event.eventId(), ex.getMessage(), ex);
        }
    }
}
