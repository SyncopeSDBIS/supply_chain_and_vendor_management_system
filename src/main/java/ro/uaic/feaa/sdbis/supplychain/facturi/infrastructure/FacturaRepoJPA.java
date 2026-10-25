package ro.uaic.feaa.sdbis.supplychain.facturi.infrastructure;

import org.springframework.stereotype.Repository;
import ro.uaic.feaa.sdbis.supplychain.facturi.domain.FacturaFurnizor;
import ro.uaic.feaa.sdbis.supplychain.facturi.domain.FacturaRepo;
import ro.uaic.feaa.sdbis.supplychain.shared.AbstractRepositoryJPA;
import java.util.UUID;

@Repository
public class FacturaRepoJPA extends AbstractRepositoryJPA<FacturaFurnizor, UUID> implements FacturaRepo {
    public FacturaRepoJPA() {
        super(FacturaFurnizor.class);
    }
}
