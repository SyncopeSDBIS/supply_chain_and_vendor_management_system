package ro.uaic.feaa.sdbis.supplychain.comenzi.infrastructure;

import org.springframework.stereotype.Repository;
import ro.uaic.feaa.sdbis.supplychain.comenzi.domain.ComandaAprovizionare;
import ro.uaic.feaa.sdbis.supplychain.comenzi.domain.ComandaRepo;
import ro.uaic.feaa.sdbis.supplychain.shared.AbstractRepositoryJPA;
import ro.uaic.feaa.sdbis.supplychain.shared.IdComanda;

@Repository
public class ComandaRepoJPA extends AbstractRepositoryJPA<ComandaAprovizionare, IdComanda> implements ComandaRepo {

    public ComandaRepoJPA() {
        super(ComandaAprovizionare.class);
    }

    @Override
    public boolean existaNumarComanda(String numarComanda) {
        Long count = em.createQuery("SELECT COUNT(c) FROM ComandaAprovizionare c WHERE c.numarComanda = :nr", Long.class)
            .setParameter("nr", numarComanda)
            .getSingleResult();
        return count != null && count > 0;
    }
}
