package ro.uaic.feaa.sdbis.supplychain.comenzi.domain;

import ro.uaic.feaa.sdbis.supplychain.shared.IdFurnizor;

/**
 * Port din Domain Layer pentru verificarea eligibilitatii furnizorului (Figura 11).
 */
public interface IGestiuneFurnizoriAdapter {
    boolean esteFurnizorEligibil(IdFurnizor id);
}
