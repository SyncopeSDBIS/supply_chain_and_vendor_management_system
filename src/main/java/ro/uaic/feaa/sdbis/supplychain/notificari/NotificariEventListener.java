package ro.uaic.feaa.sdbis.supplychain.notificari;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import ro.uaic.feaa.sdbis.supplychain.calitate.api.event.CertificareInspectorExpiraPublicEvent;
import ro.uaic.feaa.sdbis.supplychain.calitate.api.event.FurnizorSuspendatPublicEvent;
import ro.uaic.feaa.sdbis.supplychain.calitate.api.event.NCREscaladatPublicEvent;
import ro.uaic.feaa.sdbis.supplychain.comenzi.api.event.ComandaAprobataPublicEvent;
import ro.uaic.feaa.sdbis.supplychain.comenzi.api.event.ComandaConfirmataPublicEvent;
import ro.uaic.feaa.sdbis.supplychain.comenzi.api.event.ComandaCreataPublicEvent;
import ro.uaic.feaa.sdbis.supplychain.comenzi.api.event.ComandaExpediataPublicEvent;
import ro.uaic.feaa.sdbis.supplychain.comenzi.api.event.ComandaInchisaPublicEvent;
import ro.uaic.feaa.sdbis.supplychain.comenzi.api.event.ComandaNeconfirmataPublicEvent;
import ro.uaic.feaa.sdbis.supplychain.comenzi.api.event.ComandaRefuzataPublicEvent;
import ro.uaic.feaa.sdbis.supplychain.comenzi.api.event.EstimareDepasesteCerereaPublicEvent;
import ro.uaic.feaa.sdbis.supplychain.comenzi.api.event.LotCarantinatPublicEvent;
import ro.uaic.feaa.sdbis.supplychain.comenzi.api.event.LotRespinsPublicEvent;
import ro.uaic.feaa.sdbis.supplychain.comenzi.api.event.LotReturnatPublicEvent;
import ro.uaic.feaa.sdbis.supplychain.facturi.api.event.ScadentaApropiataPublicEvent;
import ro.uaic.feaa.sdbis.supplychain.shared.InboxRepo;

import java.util.UUID;

/**
 * Event Listener pentru Bounded Context Generic: Notificari si Alerte (Figura 1 Context Map).
 * Consuma evenimentele publice din pachetele api.event ale fiecarui bounded context.
 * Include protectie try-catch (exception shielding) si deduplicare Inbox pentru decuplarea tranzactiilor de business (FIX MSG-02).
 */
@Component
public class NotificariEventListener {

    private static final Logger log = LoggerFactory.getLogger(NotificariEventListener.class);
    private static final String CONSUMER_ID = "NotificariEventListener";

    private final NotificariDispatcherService dispatcher;
    private final InboxRepo inboxRepo;

    public NotificariEventListener(NotificariDispatcherService dispatcher,
                                  @Autowired(required = false) InboxRepo inboxRepo) {
        this.dispatcher = dispatcher;
        this.inboxRepo = inboxRepo;
    }

    private boolean shouldProcess(UUID eventId) {
        if (inboxRepo == null || eventId == null) {
            return true;
        }
        return inboxRepo.incearcaInregistrare(CONSUMER_ID, eventId);
    }

    @EventListener
    public void onComandaCreata(ComandaCreataPublicEvent event) {
        if (!shouldProcess(event.eventId())) return;
        try {
            dispatcher.trimiteAlertaComandaCreata(event.comandaId(), event.furnizorId());
        } catch (Exception ex) {
            log.error("Esec la expedierea alertei pentru ComandaCreataPublicEvent: {}", ex.getMessage(), ex);
        }
    }

    @EventListener
    public void onComandaAprobata(ComandaAprobataPublicEvent event) {
        if (!shouldProcess(event.eventId())) return;
        try {
            dispatcher.trimiteAlertaComandaAprobata(event.comandaId(), event.furnizorId(), event.nivelAprobare(), event.valoareTotala());
        } catch (Exception ex) {
            log.error("Esec la expedierea alertei pentru ComandaAprobataPublicEvent: {}", ex.getMessage(), ex);
        }
    }

    @EventListener
    public void onComandaConfirmata(ComandaConfirmataPublicEvent event) {
        if (!shouldProcess(event.eventId())) return;
        try {
            dispatcher.trimiteAlertaComandaConfirmata(event.comandaId(), event.furnizorId(), event.dataPromisa());
        } catch (Exception ex) {
            log.error("Esec la expedierea alertei pentru ComandaConfirmataPublicEvent: {}", ex.getMessage(), ex);
        }
    }

    @EventListener
    public void onComandaRefuzata(ComandaRefuzataPublicEvent event) {
        if (!shouldProcess(event.eventId())) return;
        try {
            dispatcher.trimiteAlertaComandaRefuzata(event.comandaId(), event.furnizorId(), event.motiv());
        } catch (Exception ex) {
            log.error("Esec la expedierea alertei pentru ComandaRefuzataPublicEvent: {}", ex.getMessage(), ex);
        }
    }

    @EventListener
    public void onComandaExpediata(ComandaExpediataPublicEvent event) {
        if (!shouldProcess(event.eventId())) return;
        try {
            dispatcher.trimiteAlertaComandaExpediata(event.comandaId(), event.transportator(), event.awb(), event.loturi().size());
        } catch (Exception ex) {
            log.error("Esec la expedierea alertei pentru ComandaExpediataPublicEvent: {}", ex.getMessage(), ex);
        }
    }

    @EventListener
    public void onLotCarantinat(LotCarantinatPublicEvent event) {
        if (!shouldProcess(event.eventId())) return;
        try {
            dispatcher.trimiteAlertaLotCarantinat(event.comandaId(), event.lotId());
        } catch (Exception ex) {
            log.error("Esec la expedierea alertei pentru LotCarantinatPublicEvent: {}", ex.getMessage(), ex);
        }
    }

    @EventListener
    public void onLotRespins(LotRespinsPublicEvent event) {
        if (!shouldProcess(event.eventId())) return;
        try {
            dispatcher.trimiteAlertaLotRespins(event.comandaId(), event.lotId(), event.motiv());
        } catch (Exception ex) {
            log.error("Esec la expedierea alertei pentru LotRespinsPublicEvent: {}", ex.getMessage(), ex);
        }
    }

    @EventListener
    public void onLotReturnat(LotReturnatPublicEvent event) {
        if (!shouldProcess(event.eventId())) return;
        try {
            dispatcher.trimiteAlertaLotReturnat(event.comandaId(), event.lotId(), event.furnizorId());
        } catch (Exception ex) {
            log.error("Esec la expedierea alertei pentru LotReturnatPublicEvent: {}", ex.getMessage(), ex);
        }
    }

    @EventListener
    public void onComandaInchisa(ComandaInchisaPublicEvent event) {
        if (!shouldProcess(event.eventId())) return;
        try {
            dispatcher.trimiteAlertaComandaInchisa(event.comandaId(), event.furnizorId());
        } catch (Exception ex) {
            log.error("Esec la expedierea alertei pentru ComandaInchisaPublicEvent: {}", ex.getMessage(), ex);
        }
    }

    @EventListener
    public void onComandaNeconfirmata(ComandaNeconfirmataPublicEvent event) {
        if (!shouldProcess(event.eventId())) return;
        try {
            dispatcher.trimiteAlertaNeconfirmareComanda(event.comandaId().getValue());
        } catch (Exception ex) {
            log.error("Esec la expedierea alertei pentru ComandaNeconfirmataPublicEvent: {}", ex.getMessage(), ex);
        }
    }

    @EventListener
    public void onEstimareDepasesteCererea(EstimareDepasesteCerereaPublicEvent event) {
        if (!shouldProcess(event.eventId())) return;
        try {
            dispatcher.trimiteAlertaLeadTimeDepasit(event.comandaId().getValue());
        } catch (Exception ex) {
            log.error("Esec la expedierea alertei pentru EstimareDepasesteCerereaPublicEvent: {}", ex.getMessage(), ex);
        }
    }

    @EventListener
    public void onNCREscaladat(NCREscaladatPublicEvent event) {
        if (!shouldProcess(event.eventId())) return;
        try {
            dispatcher.trimiteAlertaNCREscaladat(event.ncrId(), event.furnizorId().getValue());
        } catch (Exception ex) {
            log.error("Esec la expedierea alertei pentru NCREscaladatPublicEvent: {}", ex.getMessage(), ex);
        }
    }

    @EventListener
    public void onScadentaApropiata(ScadentaApropiataPublicEvent event) {
        if (!shouldProcess(event.eventId())) return;
        try {
            dispatcher.trimiteAlertaScadentaApropiata(event.facturaId());
        } catch (Exception ex) {
            log.error("Esec la expedierea alertei pentru ScadentaApropiataPublicEvent: {}", ex.getMessage(), ex);
        }
    }

    @EventListener
    public void onFurnizorSuspendat(FurnizorSuspendatPublicEvent event) {
        if (!shouldProcess(event.eventId())) return;
        try {
            dispatcher.trimiteAlertaFurnizorSuspendat(event.furnizorId().getValue(), event.motiv());
        } catch (Exception ex) {
            log.error("Esec la expedierea alertei pentru FurnizorSuspendatPublicEvent: {}", ex.getMessage(), ex);
        }
    }

    @EventListener
    public void onCertificareInspectorExpira(CertificareInspectorExpiraPublicEvent event) {
        if (!shouldProcess(event.eventId())) return;
        try {
            dispatcher.trimiteAlertaCertificareInspectorExpira(event.inspectorId(), event.denumireCertificare(), event.dataExpirare());
        } catch (Exception ex) {
            log.error("Esec la expedierea alertei pentru CertificareInspectorExpiraPublicEvent: {}", ex.getMessage(), ex);
        }
    }
}
