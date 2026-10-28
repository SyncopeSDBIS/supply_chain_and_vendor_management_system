package ro.uaic.feaa.sdbis.supplychain.notificari;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import ro.uaic.feaa.sdbis.supplychain.calitate.api.event.CertificareInspectorExpiraPublicEvent;
import ro.uaic.feaa.sdbis.supplychain.calitate.api.event.FurnizorSuspendatPublicEvent;
import ro.uaic.feaa.sdbis.supplychain.calitate.api.event.NCREscaladatPublicEvent;
import ro.uaic.feaa.sdbis.supplychain.comenzi.api.event.ComandaNeconfirmataPublicEvent;
import ro.uaic.feaa.sdbis.supplychain.comenzi.api.event.EstimareDepasesteCerereaPublicEvent;
import ro.uaic.feaa.sdbis.supplychain.facturi.api.event.ScadentaApropiataPublicEvent;

/**
 * Event Listener pentru Bounded Context Generic: Notificari si Alerte (Figura 1 Context Map).
 * Consuma evenimentele publice din pachetele api.event ale fiecarui bounded context.
 */
@Component
public class NotificariEventListener {

    private final NotificariDispatcherService dispatcher;

    public NotificariEventListener(NotificariDispatcherService dispatcher) {
        this.dispatcher = dispatcher;
    }

    @EventListener
    public void onComandaNeconfirmata(ComandaNeconfirmataPublicEvent event) {
        dispatcher.trimiteAlertaNeconfirmareComanda(event.comandaId().getValue());
    }

    @EventListener
    public void onEstimareDepasesteCererea(EstimareDepasesteCerereaPublicEvent event) {
        dispatcher.trimiteAlertaLeadTimeDepasit(event.comandaId().getValue());
    }

    @EventListener
    public void onNCREscaladat(NCREscaladatPublicEvent event) {
        dispatcher.trimiteAlertaNCREscaladat(event.ncrId(), event.furnizorId().getValue());
    }

    @EventListener
    public void onScadentaApropiata(ScadentaApropiataPublicEvent event) {
        dispatcher.trimiteAlertaScadentaApropiata(event.facturaId());
    }

    @EventListener
    public void onFurnizorSuspendat(FurnizorSuspendatPublicEvent event) {
        dispatcher.trimiteAlertaFurnizorSuspendat(event.furnizorId().getValue(), event.motiv());
    }

    @EventListener
    public void onCertificareInspectorExpira(CertificareInspectorExpiraPublicEvent event) {
        dispatcher.trimiteAlertaCertificareInspectorExpira(event.inspectorId(), event.denumireCertificare(), event.dataExpirare());
    }
}
