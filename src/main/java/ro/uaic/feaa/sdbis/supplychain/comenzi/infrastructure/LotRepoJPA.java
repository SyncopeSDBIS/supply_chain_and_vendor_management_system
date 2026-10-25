package ro.uaic.feaa.sdbis.supplychain.comenzi.infrastructure;

import org.springframework.stereotype.Repository;
import ro.uaic.feaa.sdbis.supplychain.comenzi.domain.Lot;
import ro.uaic.feaa.sdbis.supplychain.comenzi.domain.LotRepo;
import ro.uaic.feaa.sdbis.supplychain.shared.AbstractRepositoryJPA;
import ro.uaic.feaa.sdbis.supplychain.shared.IdComanda;
import ro.uaic.feaa.sdbis.supplychain.shared.IdLot;
import java.util.List;

@Repository
public class LotRepoJPA extends AbstractRepositoryJPA<Lot, IdLot> implements LotRepo {

    public LotRepoJPA() {
        super(Lot.class);
    }

    @Override
    public List<Lot> findAllByComandaId(IdComanda id) {
        return em.createQuery("SELECT l FROM ComandaAprovizionare c JOIN c.loturi l WHERE c.id = :comandaId", Lot.class)
            .setParameter("comandaId", id)
            .getResultList();
    }
}
