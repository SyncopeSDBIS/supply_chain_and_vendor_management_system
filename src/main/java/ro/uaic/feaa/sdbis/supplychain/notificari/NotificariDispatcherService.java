package ro.uaic.feaa.sdbis.supplychain.notificari;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Dispatcher Notificari si Alerte (Generic Subdomain - Context Map Figura 1).
 */
@Service
public class NotificariDispatcherService {

    private static final Logger log = LoggerFactory.getLogger(NotificariDispatcherService.class);

    public void trimiteAlertaNeconfirmareComanda(UUID comandaId) {
        log.warn("[ALERTA EMAIL/PUSH] Comanda {} nu a fost confirmata in 48 ore!", comandaId);
    }

    public void trimiteAlertaLeadTimeDepasit(UUID comandaId) {
        log.warn("[ALERTA EMAIL/PUSH] Estimarea datei de livrare pentru comanda {} depaseste data ceruta de cumparator!", comandaId);
    }

    public void trimiteAlertaNCREscaladat(UUID ncrId, UUID furnizorId) {
        log.warn("[ALERTA EMAIL/PUSH] Raportul de neconformitate {} pentru furnizorul {} a fost escaladat la management!", ncrId, furnizorId);
    }

    public void trimiteAlertaScadentaApropiata(UUID facturaId) {
        log.warn("[ALERTA EMAIL/PUSH] Factura {} are scadenta in 3 zile si nu este platita integral!", facturaId);
    }

    public void trimiteAlertaFurnizorSuspendat(UUID furnizorId, String motiv) {
        log.warn("[ALERTA EMAIL/PUSH] Furnizorul {} a fost suspendat! Motiv: {}", furnizorId, motiv);
    }

    public void trimiteAlertaCertificareInspectorExpira(UUID inspectorId, String denumire, LocalDate dataExpirare) {
        log.warn("[ALERTA EMAIL/PUSH] Certificarea '{}' a inspectorului {} expira la {}!", denumire, inspectorId, dataExpirare);
    }
}
