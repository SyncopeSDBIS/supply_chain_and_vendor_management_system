package ro.uaic.feaa.sdbis.supplychain.calitate.infrastructure;

import org.springframework.stereotype.Repository;
import ro.uaic.feaa.sdbis.supplychain.calitate.domain.NCRRepo;
import ro.uaic.feaa.sdbis.supplychain.calitate.domain.RaportNeconformitate;
import ro.uaic.feaa.sdbis.supplychain.shared.AbstractRepositoryJPA;
import java.util.UUID;

@Repository
public class NCRRepoJPA extends AbstractRepositoryJPA<RaportNeconformitate, UUID> implements NCRRepo {
    public NCRRepoJPA() {
        super(RaportNeconformitate.class);
    }
}
