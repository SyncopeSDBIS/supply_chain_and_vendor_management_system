package ro.uaic.feaa.sdbis.supplychain.notificari;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import ro.uaic.feaa.sdbis.supplychain.calitate.domain.event.CertificareInspectorExpiraEvent;
import ro.uaic.feaa.sdbis.supplychain.calitate.domain.event.FurnizorSuspendatEvent;
import ro.uaic.feaa.sdbis.supplychain.calitate.domain.event.NCREscaladatEvent;
import ro.uaic.feaa.sdbis.supplychain.comenzi.domain.event.ComandaNeconfirmataEvent;
import ro.uaic.feaa.sdbis.supplychain.comenzi.domain.event.EstimareDepasesteCerereaEvent;
import ro.uaic.feaa.sdbis.supplychain.facturi.domain.event.ScadentaApropiataEvent;

/**
 * Event Listener pentru Bounded Context Generic: Notificari si Alerte (Figura 1 Context Map).
 */
@Component
public class NotificariEventListener {

    private final NotificariDispatcherService dispatcher;

    public NotificariEventListener(NotificariDispatcherService dispatcher) {
        this.dispatcher = dispatcher;
    }

    @EventListener
    public void onComandaNeconfirmata(ComandaNeconfirmataEvent event) {
        dispatcher.trimiteAlertaNeconfirmareComanda(event.comandaId().getValue());
    }

    @EventListener
    public void onEstimareDepasesteCererea(EstimareDepasesteCerereaEvent event) {
        dispatcher.trimiteAlertaLeadTimeDepasit(event.comandaId().getValue());
    }

    @EventListener
    public void onNCREscaladat(NCREscaladatEvent event) {
        dispatcher.trimiteAlertaNCREscaladat(event.ncrId(), event.furnizorId().getValue());
    }

    @EventListener
    public void onScadentaApropiata(ScadentaApropiataEvent event) {
        dispatcher.trimiteAlertaScadentaApropiata(event.facturaId());
    }

    @EventListener
    public void onFurnizorSuspendat(FurnizorSuspendatEvent event) {
        dispatcher.trimiteAlertaFurnizorSuspendat(event.furnizorId().getValue(), event.motiv());
    }

    @EventListener
    public void onCertificareInspectorExpira(CertificareInspectorExpiraEvent event) {
        dispatcher.trimiteAlertaCertificareInspectorExpira(event.inspectorId(), event.denumireCertificare(), event.dataExpirare());
    }
}
