package ro.uaic.feaa.sdbis.supplychain.calitate.infrastructure.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import ro.uaic.feaa.sdbis.supplychain.calitate.application.CalitateApplicationService;
import ro.uaic.feaa.sdbis.supplychain.calitate.domain.PlanAQL;
import ro.uaic.feaa.sdbis.supplychain.comenzi.domain.event.ComandaReceptionataEvent;
import ro.uaic.feaa.sdbis.supplychain.shared.IdFurnizor;
import ro.uaic.feaa.sdbis.supplychain.shared.InboxRepo;
import ro.uaic.feaa.sdbis.supplychain.shared.Perioada;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Event Listener asincron pentru Bounded Context Calitate (Figura 1 Context Map).
 * La receptia marfii (ComandaReceptionataEvent), programeaza automat inspectia de calitate conform planului AQL.
 */
@Component
public class CalitateEventListener {

    private static final Logger log = LoggerFactory.getLogger(CalitateEventListener.class);
    private static final String CONSUMER_ID = "CalitateEventListener";

    private final CalitateApplicationService calitateAppService;
    private final InboxRepo inboxRepo;

    public CalitateEventListener(CalitateApplicationService calitateAppService, InboxRepo inboxRepo) {
        this.calitateAppService = calitateAppService;
        this.inboxRepo = inboxRepo;
    }

    @EventListener
    public void onComandaReceptionata(ComandaReceptionataEvent event) {
        if (inboxRepo.existaEveniment(CONSUMER_ID, event.eventId())) {
            log.info("[CalitateEventListener] Event {} already processed, skipping (Idempotent Inbox).", event.eventId());
            return;
        }

        try {
            LocalDate azi = LocalDate.now();
            Perioada perioadaInspectie = new Perioada(azi, azi.plusDays(3));
            PlanAQL plan = PlanAQL.standard(2.5, 30, 2);

            UUID inspectieId = calitateAppService.programeazaInspectie(
                event.lotId(),
                event.comandaId(),
                IdFurnizor.random(),
                event.material(),
                plan,
                perioadaInspectie
            );

            inboxRepo.marcheazaProcesat(CONSUMER_ID, event.eventId());
            log.info("[CalitateEventListener] Scheduled inspection {} for lot {} from order {}",
                inspectieId, event.lotId().getValue(), event.comandaId().getValue());
        } catch (Exception ex) {
            log.error("[CalitateEventListener] Error scheduling inspection for event {}: {}", event.eventId(), ex.getMessage(), ex);
        }
    }
}
