package ro.uaic.feaa.sdbis.supplychain.facturi.infrastructure;

import org.springframework.stereotype.Repository;
import ro.uaic.feaa.sdbis.supplychain.facturi.domain.OrdinPlata;
import ro.uaic.feaa.sdbis.supplychain.facturi.domain.OrdinPlataRepo;
import ro.uaic.feaa.sdbis.supplychain.shared.AbstractRepositoryJPA;
import java.util.UUID;

@Repository
public class OrdinPlataRepoJPA extends AbstractRepositoryJPA<OrdinPlata, UUID> implements OrdinPlataRepo {
    public OrdinPlataRepoJPA() {
        super(OrdinPlata.class);
    }
}
