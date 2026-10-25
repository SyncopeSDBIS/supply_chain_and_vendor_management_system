package ro.uaic.feaa.sdbis.supplychain.calitate.infrastructure;

import org.springframework.stereotype.Repository;
import ro.uaic.feaa.sdbis.supplychain.calitate.domain.InspectieCalitate;
import ro.uaic.feaa.sdbis.supplychain.calitate.domain.InspectieRepo;
import ro.uaic.feaa.sdbis.supplychain.shared.AbstractRepositoryJPA;
import java.util.UUID;

@Repository
public class InspectieRepoJPA extends AbstractRepositoryJPA<InspectieCalitate, UUID> implements InspectieRepo {
    public InspectieRepoJPA() {
        super(InspectieCalitate.class);
    }
}
