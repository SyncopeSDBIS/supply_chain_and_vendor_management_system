package ro.uaic.feaa.sdbis.supplychain.facturi.infrastructure.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import ro.uaic.feaa.sdbis.supplychain.calitate.domain.event.FurnizorBlocatEvent;
import ro.uaic.feaa.sdbis.supplychain.comenzi.domain.event.ComandaAnulataEvent;
import ro.uaic.feaa.sdbis.supplychain.comenzi.domain.event.ComandaReceptionataEvent;
import ro.uaic.feaa.sdbis.supplychain.facturi.application.FacturaApplicationService;
import ro.uaic.feaa.sdbis.supplychain.facturi.application.command.GenereazaNIRCommand;
import ro.uaic.feaa.sdbis.supplychain.facturi.domain.LinieNIR;
import ro.uaic.feaa.sdbis.supplychain.shared.InboxRepo;

import java.time.LocalDate;
import java.util.Collections;

/**
 * Event Listener asincron pentru Bounded Context Facturi si Plati (Figura 1 Context Map).
 * - La ComandaReceptionataEvent -> genereaza NIR cu garantie de idempotenta (ADR-009).
 * - La ComandaAnulataEvent -> anuleaza / blocheaza facturile comenzii.
 * - La FurnizorBlocatEvent -> blocheaza platile catre furnizor.
 */
@Component
public class FacturaEventListener {

    private static final Logger log = LoggerFactory.getLogger(FacturaEventListener.class);
    private static final String CONSUMER_ID = "FacturaEventListener";

    private final FacturaApplicationService facturaAppService;
    private final InboxRepo inboxRepo;

    public FacturaEventListener(FacturaApplicationService facturaAppService, InboxRepo inboxRepo) {
        this.facturaAppService = facturaAppService;
        this.inboxRepo = inboxRepo;
    }

    @EventListener
    public void onComandaReceptionata(ComandaReceptionataEvent event) {
        if (inboxRepo.existaEveniment(CONSUMER_ID, event.eventId())) {
            log.info("[FacturaEventListener] Event {} already processed, skipping (Idempotent Inbox).", event.eventId());
            return;
        }

        try {
            String numarNIR = "NIR-" + event.comandaId().getValue().toString().substring(0, 8).toUpperCase()
                + "-" + event.lotId().getValue().toString().substring(0, 4).toUpperCase();

            LinieNIR linie = new LinieNIR(
                event.lotId(), event.material(), event.cantitatePrimita().getValoare(), event.cantitatePrimita().getUnitateMasura()
            );
            GenereazaNIRCommand cmd = new GenereazaNIRCommand(
                numarNIR,
                event.comandaId(),
                event.eventId(),
                LocalDate.now(),
                Collections.singletonList(linie)
            );

            facturaAppService.genereazaNIR(cmd);
            inboxRepo.marcheazaProcesat(CONSUMER_ID, event.eventId());
            log.info("[FacturaEventListener] Successfully generated NIR {} for order {}", numarNIR, event.comandaId().getValue());
        } catch (Exception ex) {
            log.error("[FacturaEventListener] Error generating NIR for event {}: {}", event.eventId(), ex.getMessage(), ex);
        }
    }

    @EventListener
    public void onComandaAnulata(ComandaAnulataEvent event) {
        if (inboxRepo.existaEveniment(CONSUMER_ID, event.eventId())) return;

        try {
            facturaAppService.blocheazaFacturiComandaAnulata(event.comandaId());
            inboxRepo.marcheazaProcesat(CONSUMER_ID, event.eventId());
            log.info("[FacturaEventListener] Cancelled invoices for cancelled order {}", event.comandaId().getValue());
        } catch (Exception ex) {
            log.error("[FacturaEventListener] Error cancelling invoices for order {}: {}", event.comandaId().getValue(), ex.getMessage(), ex);
        }
    }

    @EventListener
    public void onFurnizorBlocat(FurnizorBlocatEvent event) {
        if (inboxRepo.existaEveniment(CONSUMER_ID, event.eventId())) return;

        try {
            facturaAppService.blocheazaPlatiFurnizor(event.furnizorId());
            inboxRepo.marcheazaProcesat(CONSUMER_ID, event.eventId());
            log.info("[FacturaEventListener] Blocked payments for blocked supplier {}", event.furnizorId().getValue());
        } catch (Exception ex) {
            log.error("[FacturaEventListener] Error blocking payments for supplier {}: {}", event.furnizorId().getValue(), ex.getMessage(), ex);
        }
    }
}
