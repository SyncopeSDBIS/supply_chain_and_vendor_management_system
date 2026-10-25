package ro.uaic.feaa.sdbis.supplychain.facturi.domain.validation;

import ro.uaic.feaa.sdbis.supplychain.facturi.domain.ComandaSummaryVO;
import ro.uaic.feaa.sdbis.supplychain.facturi.domain.FacturaFurnizor;
import ro.uaic.feaa.sdbis.supplychain.facturi.domain.LinieSumar;
import ro.uaic.feaa.sdbis.supplychain.facturi.domain.NIR;
import ro.uaic.feaa.sdbis.supplychain.facturi.domain.StareFactura;
import ro.uaic.feaa.sdbis.supplychain.shared.exception.DomainValidationException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

/**
 * Implementare Domain Validation Service pentru Facturi si 3-Way Matching (Sprint 2 - ADR-003).
 * Respecta izolarea pura de domeniu si separarea de context Bounded Context (ADR-011).
 */
public class FacturaValidationService implements IFacturaValidationService {

    @Override
    public void valideazaFacturaInregistrare(FacturaFurnizor factura) {
        Objects.requireNonNull(factura, "Factura nu poate fi null!");

        if (factura.getSerie() == null || factura.getSerie().trim().isEmpty()) {
            throw new DomainValidationException("SERIE_FACTURA_LIPSA", "Seria facturii este obligatorie!");
        }
        if (factura.getNumar() == null || factura.getNumar().trim().isEmpty()) {
            throw new DomainValidationException("NUMAR_FACTURA_LIPSA", "Numarul facturii este obligatoriu!");
        }
        if (factura.getDataScadenta().isBefore(factura.getDataEmitere())) {
            throw new DomainValidationException("SCADENTA_INVALIDA", "Data scadenta nu poate fi anterioara datei emiterii facturii!");
        }
        if (factura.getLinii() == null || factura.getLinii().isEmpty()) {
            throw new DomainValidationException("LINII_FACTURA_LIPSA", "Factura trebuie sa contina cel putin o linie!");
        }
        if (factura.getValoare() == null || factura.getValoare().getValoareTotala().compareTo(BigDecimal.ZERO) <= 0) {
            throw new DomainValidationException("VALOARE_FACTURA_INVALIDA", "Valoarea totala a facturii trebuie sa fie strict pozitiva!");
        }
    }

    @Override
    public void valideaza3WayMatch(FacturaFurnizor factura, NIR nir, ComandaSummaryVO comanda, double tolerantaProcentualaMax) {
        Objects.requireNonNull(factura, "Factura nu poate fi null!");
        Objects.requireNonNull(nir, "NIR-ul nu poate fi null!");
        Objects.requireNonNull(comanda, "Comanda nu poate fi null!");

        if (!factura.getComandaId().equals(comanda.comandaId())) {
            throw new DomainValidationException("COMANDA_FACTURA_MISMATCH",
                "Factura referentiaza comanda " + factura.getComandaId().getValue() + ", dar s-a transmis comanda " + comanda.comandaId().getValue());
        }
        if (!nir.getComandaId().equals(comanda.comandaId())) {
            throw new DomainValidationException("COMANDA_NIR_MISMATCH",
                "NIR-ul referentiaza comanda " + nir.getComandaId().getValue() + ", dar s-a transmis comanda " + comanda.comandaId().getValue());
        }

        BigDecimal totalComandaFaraTVA = BigDecimal.ZERO;
        if (comanda.linii() != null) {
            for (LinieSumar linie : comanda.linii()) {
                BigDecimal valLinie = linie.cantitateComandata().multiply(linie.pretUnitar());
                totalComandaFaraTVA = totalComandaFaraTVA.add(valLinie);
            }
        }

        BigDecimal totalFacturaFaraTVA = factura.getValoare().getValoareFaraTVA();

        if (totalComandaFaraTVA.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal diferenta = totalFacturaFaraTVA.subtract(totalComandaFaraTVA).abs();
            BigDecimal procentDiferenta = diferenta.divide(totalComandaFaraTVA, 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100));

            if (procentDiferenta.doubleValue() > tolerantaProcentualaMax) {
                throw new DomainValidationException("DISCREPANTA_3WAY_MATCH",
                    String.format("Discrepanta financiara 3-way match (%.2f%%) depaseste toleranta admisa de %.2f%%! Comanda: %s RON, Factura: %s RON",
                        procentDiferenta.doubleValue(), tolerantaProcentualaMax, totalComandaFaraTVA, totalFacturaFaraTVA));
            }
        }
    }

    @Override
    public void valideazaOrdinPlata(FacturaFurnizor factura, BigDecimal sumaPlata) {
        Objects.requireNonNull(factura, "Factura nu poate fi null!");
        if (sumaPlata == null || sumaPlata.compareTo(BigDecimal.ZERO) <= 0) {
            throw new DomainValidationException("SUMA_PLATA_INVALIDA", "Suma ordinului de plata trebuie sa fie strict pozitiva!");
        }

        StareFactura stare = factura.getStareFactura();
        if (stare != StareFactura.AprobataLaPlata && stare != StareFactura.PartialPlatita) {
            throw new DomainValidationException("STARE_FACTURA_INELIGIBILA_PLATA",
                "Ordinul de plata poate fi emis doar pentru facturi AprobataLaPlata sau PartialPlatita! Stare curenta: " + stare);
        }

        BigDecimal restDePlata = factura.calculeazaRestDePlata().getValoare();
        if (sumaPlata.compareTo(restDePlata) > 0) {
            throw new DomainValidationException("SUMA_DEPASESTE_RESTUL",
                "Suma ordinului de plata (" + sumaPlata + ") depaseste restul de plata al facturii (" + restDePlata + ")!");
        }
    }
}
