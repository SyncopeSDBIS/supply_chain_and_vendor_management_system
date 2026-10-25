package ro.uaic.feaa.sdbis.supplychain.calitate.infrastructure;

import org.springframework.stereotype.Repository;
import ro.uaic.feaa.sdbis.supplychain.calitate.domain.InspectorCalitate;
import ro.uaic.feaa.sdbis.supplychain.calitate.domain.InspectorRepo;
import ro.uaic.feaa.sdbis.supplychain.shared.AbstractRepositoryJPA;
import java.util.UUID;

@Repository
public class InspectorRepoJPA extends AbstractRepositoryJPA<InspectorCalitate, UUID> implements InspectorRepo {
    public InspectorRepoJPA() {
        super(InspectorCalitate.class);
    }
}
