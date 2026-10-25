package ro.uaic.feaa.sdbis.supplychain.calitate.infrastructure;

import org.springframework.stereotype.Repository;
import ro.uaic.feaa.sdbis.supplychain.calitate.domain.Furnizor;
import ro.uaic.feaa.sdbis.supplychain.calitate.domain.FurnizorRepo;
import ro.uaic.feaa.sdbis.supplychain.shared.AbstractRepositoryJPA;
import ro.uaic.feaa.sdbis.supplychain.shared.IdFurnizor;

@Repository
public class FurnizorRepoJPA extends AbstractRepositoryJPA<Furnizor, IdFurnizor> implements FurnizorRepo {
    public FurnizorRepoJPA() {
        super(Furnizor.class);
    }
}
