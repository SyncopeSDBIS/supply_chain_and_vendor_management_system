package ro.uaic.feaa.sdbis.supplychain.notificari;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ro.uaic.feaa.sdbis.supplychain.shared.annotation.UtilityService;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedDeque;

/**
 * Dispatcher Enterprise Notificari si Alerte (Generic Subdomain - Context Map Figura 1).
 * Implementeaza canalul decuplat de alerte de business cu registru in memorie,
 * suport pentru canale externe prin NotificationChannel SPI si audit complet al notificarilor.
 */
@Service
@UtilityService
public class NotificariDispatcherService {

    private static final Logger log = LoggerFactory.getLogger(NotificariDispatcherService.class);

    public interface NotificationChannel {
        void send(NotificareRecord notificare);
    }

    public record NotificareRecord(
            UUID notificareId,
            Instant timestamp,
            String tipAlerta,
            UUID entitateId,
            String mesaj,
            String nivelSeveritate
    ) {}

    private final Deque<NotificareRecord> istoricNotificari = new ConcurrentLinkedDeque<>();
    private final List<NotificationChannel> channels;

    public NotificariDispatcherService(@Autowired(required = false) List<NotificationChannel> channels) {
        this.channels = channels != null ? channels : Collections.emptyList();
    }

    public void trimiteAlertaNeconfirmareComanda(UUID comandaId) {
        String msg = String.format("Comanda %s nu a fost confirmata in 48 ore!", comandaId);
        inregistreazaSiExpediaza("COM_TIMEOUT_CONFIRMARE", comandaId, msg, "WARNING");
    }

    public void trimiteAlertaLeadTimeDepasit(UUID comandaId) {
        String msg = String.format("Estimarea datei de livrare pentru comanda %s depaseste data ceruta de cumparator!", comandaId);
        inregistreazaSiExpediaza("COM_LEAD_TIME_DEPASIT", comandaId, msg, "WARNING");
    }

    public void trimiteAlertaNCREscaladat(UUID ncrId, UUID furnizorId) {
        String msg = String.format("Raportul de neconformitate %s pentru furnizorul %s a fost escaladat la management!", ncrId, furnizorId);
        inregistreazaSiExpediaza("CALITATE_NCR_ESCALADAT", ncrId, msg, "CRITICAL");
    }

    public void trimiteAlertaScadentaApropiata(UUID facturaId) {
        String msg = String.format("Factura %s are scadenta in 3 zile si nu este platita integral!", facturaId);
        inregistreazaSiExpediaza("FINANCIAR_SCADENTA_APROPIATA", facturaId, msg, "INFO");
    }

    public void trimiteAlertaFurnizorSuspendat(UUID furnizorId, String motiv) {
        String msg = String.format("Furnizorul %s a fost suspendat! Motiv: %s", furnizorId, motiv);
        inregistreazaSiExpediaza("CALITATE_FURNIZOR_SUSPENDAT", furnizorId, msg, "ERROR");
    }

    public void trimiteAlertaCertificareInspectorExpira(UUID inspectorId, String denumire, LocalDate dataExpirare) {
        String msg = String.format("Certificarea '%s' a inspectorului %s expira la %s!", denumire, inspectorId, dataExpirare);
        inregistreazaSiExpediaza("CALITATE_CERTIFICARE_EXPIRA", inspectorId, msg, "WARNING");
    }

    public void trimiteAlertaComandaCreata(UUID comandaId, UUID furnizorId) {
        String msg = String.format("Comanda %s a fost plasata catre furnizorul %s si asteapta aprobare operationala.", comandaId, furnizorId);
        inregistreazaSiExpediaza("COM_COMANDA_CREATA", comandaId, msg, "INFO");
    }

    public void trimiteAlertaComandaAprobata(UUID comandaId, UUID furnizorId, String nivel, BigDecimal total) {
        String msg = String.format("Comanda %s (Total: %s RON) a fost aprobata de nivelul %s si transmisa catre furnizorul %s.",
                comandaId, total, nivel, furnizorId);
        inregistreazaSiExpediaza("COM_COMANDA_APROBATA", comandaId, msg, "INFO");
    }

    public void trimiteAlertaComandaConfirmata(UUID comandaId, UUID furnizorId, LocalDate dataPromisa) {
        String msg = String.format("Comanda %s a fost confirmata de furnizorul %s pentru livrare la %s.", comandaId, furnizorId, dataPromisa);
        inregistreazaSiExpediaza("COM_COMANDA_CONFIRMATA", comandaId, msg, "INFO");
    }

    public void trimiteAlertaComandaRefuzata(UUID comandaId, UUID furnizorId, String motiv) {
        String msg = String.format("Comanda %s a fost refuzata de furnizorul %s! Motiv: %s", comandaId, furnizorId, motiv);
        inregistreazaSiExpediaza("COM_COMANDA_REFUZATA", comandaId, msg, "ERROR");
    }

    public void trimiteAlertaComandaExpediata(UUID comandaId, String transportator, String awb, int numarLoturi) {
        String msg = String.format("Comanda %s a fost expediata prin %s (AWB: %s) continand %d loturi.", comandaId, transportator, awb, numarLoturi);
        inregistreazaSiExpediaza("COM_COMANDA_EXPEDIATA", comandaId, msg, "INFO");
    }

    public void trimiteAlertaLotCarantinat(UUID comandaId, UUID lotId) {
        String msg = String.format("Lotul %s din comanda %s a fost plasat in carantina si blocat de la eliberare.", lotId, comandaId);
        inregistreazaSiExpediaza("COM_LOT_CARANTINAT", lotId, msg, "WARNING");
    }

    public void trimiteAlertaLotRespins(UUID comandaId, UUID lotId, String motiv) {
        String msg = String.format("Lotul %s din comanda %s a fost respins la inspectia AQL! Motiv: %s", lotId, comandaId, motiv);
        inregistreazaSiExpediaza("COM_LOT_RESPINS", lotId, msg, "ERROR");
    }

    public void trimiteAlertaLotReturnat(UUID comandaId, UUID lotId, UUID furnizorId) {
        String msg = String.format("Lotul neconform %s din comanda %s a fost returnat fizic furnizorului %s.", lotId, comandaId, furnizorId);
        inregistreazaSiExpediaza("COM_LOT_RETURNAT", lotId, msg, "WARNING");
    }

    public void trimiteAlertaComandaInchisa(UUID comandaId, UUID furnizorId) {
        String msg = String.format("Comanda de achizitie %s a fost reconciliata si inchisa financiar.", comandaId);
        inregistreazaSiExpediaza("COM_COMANDA_INCHISA", comandaId, msg, "INFO");
    }

    private void inregistreazaSiExpediaza(String tipAlerta, UUID entitateId, String mesaj, String nivel) {
        NotificareRecord record = new NotificareRecord(
                UUID.randomUUID(),
                Instant.now(),
                tipAlerta,
                entitateId,
                mesaj,
                nivel
        );
        istoricNotificari.addFirst(record);
        while (istoricNotificari.size() > 500) {
            istoricNotificari.removeLast();
        }

        log.warn("[NOTIFICARI DISPATCHER - {}] {}", nivel, mesaj);

        for (NotificationChannel channel : channels) {
            try {
                channel.send(record);
            } catch (Exception ex) {
                log.error("[Notification Channel Failed] Eroare la trimiterea alertei {} prin canalul {}: {}",
                        tipAlerta, channel.getClass().getSimpleName(), ex.getMessage());
            }
        }
    }

    public List<NotificareRecord> getIstoricNotificari() {
        return Collections.unmodifiableList(new ArrayList<>(istoricNotificari));
    }

    public List<NotificareRecord> getNotificariPentruEntitate(UUID entitateId) {
        return istoricNotificari.stream()
                .filter(n -> Objects.equals(n.entitateId(), entitateId))
                .toList();
    }

    public int getNumarNotificari() {
        return istoricNotificari.size();
    }

    public void stergeIstoric() {
        istoricNotificari.clear();
    }
}
