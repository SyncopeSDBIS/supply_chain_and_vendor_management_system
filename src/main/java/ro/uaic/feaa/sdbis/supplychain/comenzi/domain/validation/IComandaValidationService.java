package ro.uaic.feaa.sdbis.supplychain.comenzi.domain.validation;

import ro.uaic.feaa.sdbis.supplychain.comenzi.domain.ComandaAprovizionare;
import ro.uaic.feaa.sdbis.supplychain.comenzi.domain.LinieComanda;
import ro.uaic.feaa.sdbis.supplychain.comenzi.domain.StareComanda;

import java.math.BigDecimal;

/**
 * Contract pentru Serviciul de Validare a Domeniului Comenzi (ADR-003).
 * Trateaza validari complexe, cross-entity si cross-aggregate (Furnizor - Comanda).
 */
public interface IComandaValidationService {

    /**
     * Valideaza consistenta integrala a comenzii de aprovizionare si eligibilitatea furnizorului.
     */
    void valideazaComanda(ComandaAprovizionare comanda, boolean furnizorEligibil);

    /**
     * Valideaza linia individuala de comanda (cantitate > 0, pret > 0).
     */
    void valideazaLinie(LinieComanda linie);

    /**
     * Valideaza permisiunea tranzitiei catre o stare tinta.
     */
    void valideazaTranzitieStare(ComandaAprovizionare comanda, StareComanda stareTinta);

    /**
     * Valideaza operatiunea de receptie de marfa asociata comenzii.
     */
    void valideazaReceptie(ComandaAprovizionare comanda, BigDecimal cantitateReceptieTotala);
}
