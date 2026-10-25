package ro.uaic.feaa.sdbis.supplychain.facturi.infrastructure;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ro.uaic.feaa.sdbis.supplychain.facturi.domain.IBancaPlataProvider;
import ro.uaic.feaa.sdbis.supplychain.facturi.domain.OrdinPlata;
import ro.uaic.feaa.sdbis.supplychain.shared.IJurnalAudit;

import java.util.UUID;

/**
 * Adapter pentru transmiterea platilor catre banca prin Stripe Sandbox (ADR-010).
 */
@Component
public class StripeBancaPlataAdapter implements IBancaPlataProvider {

    private static final Logger log = LoggerFactory.getLogger(StripeBancaPlataAdapter.class);
    private final IJurnalAudit jurnalAudit;

    public StripeBancaPlataAdapter(IJurnalAudit jurnalAudit) {
        this.jurnalAudit = jurnalAudit;
    }

    @Override
    public boolean transmiteOrdinPlata(OrdinPlata ordin, String ibanDestinatar) {
        String bankTransferRef = "STRIPE-TRX-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        log.info("[Stripe Sandbox] Transmitting payment order {} | Sum: {} {} | Dest IBAN: {} | Bank Ref: {}",
            ordin.getIdOrdin(), ordin.getSuma().getValoare(), ordin.getSuma().getMoneda(), ibanDestinatar, bankTransferRef);
        jurnalAudit.inregistreazaAcces("TRANSMITERE_BANCA_STRIPE",
            "Ordin: " + ordin.getIdOrdin() + " | Ref: " + bankTransferRef + " | Dest: " + ibanDestinatar);
        return true;
    }
}
