package ro.uaic.feaa.sdbis.supplychain.comenzi.domain.validation;

import ro.uaic.feaa.sdbis.supplychain.comenzi.domain.ComandaAprovizionare;
import ro.uaic.feaa.sdbis.supplychain.comenzi.domain.LinieComanda;
import ro.uaic.feaa.sdbis.supplychain.comenzi.domain.StareComanda;
import ro.uaic.feaa.sdbis.supplychain.shared.annotation.DomainValidationService;
import ro.uaic.feaa.sdbis.supplychain.shared.exception.DomainValidationException;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Implementare Domain Validation Service pentru Comenzi (Sprint 2 - ADR-003).
 * Respecta izolarea pura de domeniu (ADR-011).
 */
@DomainValidationService
public class ComandaValidationService implements IComandaValidationService {

    @Override
    public void valideazaComanda(ComandaAprovizionare comanda, boolean furnizorEligibil) {
        Objects.requireNonNull(comanda, "Comanda nu poate fi null!");

        if (!furnizorEligibil) {
            throw new DomainValidationException("FURNIZOR_NEELIGIBIL",
                "Furnizorul asociat comenzii " + comanda.getNumarComanda() + " nu este eligibil/aprobat pentru plasarea de comenzi!");
        }

        if (comanda.getDataLivrareCeruta().isBefore(comanda.getDataComanda())) {
            throw new DomainValidationException("DATA_LIVRARE_INVALIDA",
                "Data de livrare ceruta (" + comanda.getDataLivrareCeruta() + ") nu poate fi anterioara datei comenzii (" + comanda.getDataComanda() + ")!");
        }

        if (comanda.getLinii() == null || comanda.getLinii().isEmpty()) {
            throw new DomainValidationException("LINII_COMANDA_GOALE",
                "Comanda " + comanda.getNumarComanda() + " trebuie sa contina cel putin o linie de comanda!");
        }

        for (LinieComanda linie : comanda.getLinii()) {
            valideazaLinie(linie);
        }
    }

    @Override
    public void valideazaLinie(LinieComanda linie) {
        Objects.requireNonNull(linie, "Linia de comanda nu poate fi null!");

        if (linie.getCantitate() == null || linie.getCantitate().getValoare() == null ||
            linie.getCantitate().getValoare().compareTo(BigDecimal.ZERO) <= 0) {
            throw new DomainValidationException("CANTITATE_INVALIDA",
                "Cantitatea comandata pentru materialul " + linie.getMaterial().getCod() + " trebuie sa fie strict pozitiva!");
        }

        if (linie.getPretUnitar() == null || linie.getPretUnitar().getValoare() == null ||
            linie.getPretUnitar().getValoare().compareTo(BigDecimal.ZERO) <= 0) {
            throw new DomainValidationException("PRET_INVALID",
                "Pretul unitar pentru materialul " + linie.getMaterial().getCod() + " trebuie sa fie strict pozitiv!");
        }
    }

    @Override
    public void valideazaTranzitieStare(ComandaAprovizionare comanda, StareComanda stareTinta) {
        Objects.requireNonNull(comanda, "Comanda nu poate fi null!");
        Objects.requireNonNull(stareTinta, "Starea tinta nu poate fi null!");

        StareComanda curenta = comanda.getStareComanda();

        if (curenta == StareComanda.Anulat) {
            throw new DomainValidationException("TRANZITIE_INTERZISA", "O comanda anulata nu mai poate suferi nicio tranzitie de stare!");
        }
        if (curenta == StareComanda.Receptionat && stareTinta == StareComanda.Anulat) {
            throw new DomainValidationException("TRANZITIE_INTERZISA", "O comanda receptionata integral nu mai poate fi anulata!");
        }

        switch (stareTinta) {
            case Confirmat, Refuzat -> {
                if (curenta != StareComanda.Solicitat) {
                    throw new DomainValidationException("TRANZITIE_INTERZISA",
                        "Comanda poate fi confirmata sau refuzata doar din starea Solicitat! Curenta: " + curenta);
                }
            }
            case InTranzit -> {
                if (curenta != StareComanda.Confirmat && curenta != StareComanda.InTranzit && curenta != StareComanda.ReceptionatPartial) {
                    throw new DomainValidationException("TRANZITIE_INTERZISA",
                        "Comanda poate trece in tranzit doar din Confirmat sau ReceptionatPartial! Curenta: " + curenta);
                }
            }
            case Receptionat, ReceptionatPartial -> {
                if (curenta != StareComanda.InTranzit && curenta != StareComanda.ReceptionatPartial) {
                    throw new DomainValidationException("TRANZITIE_INTERZISA",
                        "Receptia poate fi inregistrata doar pentru comenzi in stare InTranzit sau ReceptionatPartial! Curenta: " + curenta);
                }
            }
            default -> {}
        }
    }

    @Override
    public void valideazaReceptie(ComandaAprovizionare comanda, BigDecimal cantitateReceptieTotala) {
        Objects.requireNonNull(comanda, "Comanda nu poate fi null!");
        if (cantitateReceptieTotala == null || cantitateReceptieTotala.compareTo(BigDecimal.ZERO) <= 0) {
            throw new DomainValidationException("CANTITATE_RECEPTIE_INVALIDA",
                "Cantitatea receptionata trebuie sa fie strict pozitiva!");
        }
        if (comanda.getStareComanda() != StareComanda.InTranzit &&
            comanda.getStareComanda() != StareComanda.ReceptionatPartial) {
            throw new DomainValidationException("STARE_RECEPTIE_INVALIDA",
                "Comanda nu este in stare eligibila pentru receptie! Curenta: " + comanda.getStareComanda());
        }
    }
}
