package ro.uaic.feaa.sdbis.supplychain.comenzi.infrastructure.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
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

    @EventListener
    public void onComandaReceptionata(ComandaReceptionataEvent event) {
        if (inboxRepo.existaEveniment(CONSUMER_ID, event.eventId())) {
            log.info("[ComandaReceptieEventListener] Event {} already processed, skipping.", event.eventId());
            return;
        }

        try {
            UUID inspectieId = calitateApi.programeazaInspectie(
                event.lotId().getValue(),
                event.comandaId().getValue(),
                event.material().getCod()
            );

            inboxRepo.marcheazaProcesat(CONSUMER_ID, event.eventId());
            log.info("[ComandaReceptieEventListener] Triggered inspection {} for lot {} from order {}",
                inspectieId, event.lotId().getValue(), event.comandaId().getValue());
        } catch (Exception ex) {
            log.error("[ComandaReceptieEventListener] Error triggering inspection for event {}: {}", event.eventId(), ex.getMessage(), ex);
        }
    }
}
