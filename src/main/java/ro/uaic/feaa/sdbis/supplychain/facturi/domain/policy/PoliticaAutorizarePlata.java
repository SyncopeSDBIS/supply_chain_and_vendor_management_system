package ro.uaic.feaa.sdbis.supplychain.facturi.domain.policy;

import ro.uaic.feaa.sdbis.supplychain.facturi.domain.StareFactura;
import ro.uaic.feaa.sdbis.supplychain.facturi.domain.Suma;
import ro.uaic.feaa.sdbis.supplychain.shared.annotation.DomainPolicy;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

/**
 * Politica de autorizare a platilor (Other Business Rules - ADR-003 / Project_OOAD):
 * 1. Principiul celor 4 ochi (Four-Eyes Principle): Creatorul ordinului de plata NU poate fi aprobatorul platii.
 * 2. Conditie stare factura: Factura trebuie sa fie AprobataLaPlata sau PartialPlatita.
 * 3. Prag autorizare CFO: Platile ce depasesc 20,000 RON necesita aprobarea Directorului Financiar (CFO).
 */
@DomainPolicy
public class PoliticaAutorizarePlata {

    public static final BigDecimal PRAG_CFO = BigDecimal.valueOf(20000.00);

    public boolean respectaFourEyesPrinciple(UUID creatorId, UUID aprobatorId) {
        Objects.requireNonNull(creatorId, "creatorId cannot be null");
        Objects.requireNonNull(aprobatorId, "aprobatorId cannot be null");
        return !creatorId.equals(aprobatorId);
    }

    public boolean necesitaAprobareCFO(Suma suma) {
        Objects.requireNonNull(suma, "suma cannot be null");
        return suma.getValoare().compareTo(PRAG_CFO) > 0;
    }

    public boolean esteStareFacturaEligibila(StareFactura stareFactura) {
        return stareFactura == StareFactura.AprobataLaPlata || stareFactura == StareFactura.PartialPlatita;
    }

    public void valideazaAutorizare(UUID creatorId, UUID aprobatorId, Suma suma, StareFactura stareFactura, boolean esteCFO) {
        if (!respectaFourEyesPrinciple(creatorId, aprobatorId)) {
            throw new IllegalStateException("Four-Eyes Principle incalcat: Creatorul ordinului de plata (" 
                    + creatorId + ") nu poate fi acelasi cu aprobatorul platii.");
        }

        if (!esteStareFacturaEligibila(stareFactura)) {
            throw new IllegalStateException("Factura nu este eligibila pentru plata. Stare curenta: " + stareFactura 
                    + ". Stare necesara: AprobataLaPlata sau PartialPlatita.");
        }

        if (necesitaAprobareCFO(suma) && !esteCFO) {
            throw new IllegalStateException("Plata in valoare de " + suma.getValoare() + " " + suma.getMoneda() 
                    + " depaseste pragul de " + PRAG_CFO + " si necesita aprobarea Directorului Financiar (CFO).");
        }
    }
}
