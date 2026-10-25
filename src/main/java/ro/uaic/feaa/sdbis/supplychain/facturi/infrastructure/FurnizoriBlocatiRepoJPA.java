package ro.uaic.feaa.sdbis.supplychain.facturi.infrastructure;

import org.springframework.stereotype.Repository;
import ro.uaic.feaa.sdbis.supplychain.facturi.domain.FurnizoriBlocatiRepo;
import ro.uaic.feaa.sdbis.supplychain.shared.AbstractRepositoryJPA;
import ro.uaic.feaa.sdbis.supplychain.shared.IdFurnizor;
import java.util.HashSet;
import java.util.Set;

@Repository
public class FurnizoriBlocatiRepoJPA extends AbstractRepositoryJPA<Object, Long> implements FurnizoriBlocatiRepo {

    private final Set<IdFurnizor> blocati = new HashSet<>();

    public FurnizoriBlocatiRepoJPA() {
        super(Object.class);
    }

    @Override
    public void adauga(IdFurnizor id) {
        blocati.add(id);
    }

    @Override
    public boolean esteBlocat(IdFurnizor id) {
        return blocati.contains(id);
    }
}
