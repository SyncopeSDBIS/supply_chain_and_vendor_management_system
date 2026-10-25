package ro.uaic.feaa.sdbis.supplychain.comenzi.domain.computation;

import ro.uaic.feaa.sdbis.supplychain.comenzi.domain.ComandaAprovizionare;

import java.math.BigDecimal;

/**
 * Contract pentru Serviciul de Calcul al Domeniului Comenzi (Sprint 2 - ADR-003).
 * Trateaza Entity Field Computations (totaluri, TVA, procent livrare)
 * si Entity View Computations (ComandaSummaryView).
 */
public interface IComandaComputationService {

    /**
     * Calculeaza subtotalul comenzii (fara TVA) prin agregarea liniilor.
     */
    BigDecimal calculSubtotal(ComandaAprovizionare comanda);

    /**
     * Calculeaza TVA-ul aplicabil comenzii in baza cotei furnizate (ex: 0.19 pentru 19%).
     */
    BigDecimal calculTVA(ComandaAprovizionare comanda, BigDecimal cotaTVA);

    /**
     * Calculeaza valoarea totala a comenzii incluzand TVA.
     */
    BigDecimal calculTotalCuTVA(ComandaAprovizionare comanda, BigDecimal cotaTVA);

    /**
     * Calculeaza procentul de acoperire/livrare a marfurilor (0.0 - 100.0%).
     */
    double calculProcentLivrare(ComandaAprovizionare comanda);

    /**
     * Genereaza o proiectie sumarizata (Entity View Computation) a comenzii.
     */
    ComandaSummaryView genereazaSumar(ComandaAprovizionare comanda, BigDecimal cotaTVA);
}
