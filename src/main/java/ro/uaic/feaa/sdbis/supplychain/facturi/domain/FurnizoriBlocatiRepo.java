package ro.uaic.feaa.sdbis.supplychain.facturi.domain;

import ro.uaic.feaa.sdbis.supplychain.shared.IdFurnizor;
import ro.uaic.feaa.sdbis.supplychain.shared.annotation.DomainRepository;

import java.util.Set;

@DomainRepository
public interface FurnizoriBlocatiRepo {
    void adauga(IdFurnizor id);
    boolean esteBlocat(IdFurnizor id);
    Set<IdFurnizor> obtineTotiFurnizoriiBlocati();
}
