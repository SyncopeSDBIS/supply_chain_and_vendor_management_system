package ro.uaic.feaa.sdbis.supplychain.facturi.domain;

import ro.uaic.feaa.sdbis.supplychain.shared.IdFurnizor;

public interface FurnizoriBlocatiRepo {
    void adauga(IdFurnizor id);
    boolean esteBlocat(IdFurnizor id);
}
