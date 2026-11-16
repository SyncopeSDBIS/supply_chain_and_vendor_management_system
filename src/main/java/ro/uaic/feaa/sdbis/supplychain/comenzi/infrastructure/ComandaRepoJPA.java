package ro.uaic.feaa.sdbis.supplychain.comenzi.infrastructure;

import org.springframework.stereotype.Repository;
import ro.uaic.feaa.sdbis.supplychain.comenzi.domain.ComandaAprovizionare;
import ro.uaic.feaa.sdbis.supplychain.comenzi.domain.ComandaRepo;
import ro.uaic.feaa.sdbis.supplychain.comenzi.domain.StareComanda;
import ro.uaic.feaa.sdbis.supplychain.shared.AbstractRepositoryJPA;
import ro.uaic.feaa.sdbis.supplychain.shared.IdComanda;
import ro.uaic.feaa.sdbis.supplychain.shared.IdFurnizor;

import java.util.List;
import java.util.Optional;

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

    @Override
    public List<ComandaAprovizionare> findByFurnizorId(IdFurnizor furnizorId) {
        if (furnizorId == null) {
            return findAll();
        }
        return em.createQuery("SELECT c FROM ComandaAprovizionare c WHERE c.furnizorId = :fId ORDER BY c.dataComanda DESC", ComandaAprovizionare.class)
                .setParameter("fId", furnizorId)
                .getResultList();
    }

    @Override
    public List<ComandaAprovizionare> findComenzi(IdFurnizor furnizorId, StareComanda stare, int page, int size) {
        StringBuilder jpql = new StringBuilder("SELECT c FROM ComandaAprovizionare c WHERE 1=1");
        if (furnizorId != null) {
            jpql.append(" AND c.furnizorId = :fId");
        }
        if (stare != null) {
            jpql.append(" AND c.stareComanda = :stare");
        }
        jpql.append(" ORDER BY c.dataComanda DESC");

        var query = em.createQuery(jpql.toString(), ComandaAprovizionare.class);
        if (furnizorId != null) {
            query.setParameter("fId", furnizorId);
        }
        if (stare != null) {
            query.setParameter("stare", stare);
        }
        query.setFirstResult(Math.max(0, page) * Math.max(1, size));
        query.setMaxResults(Math.max(1, size));
        List<ComandaAprovizionare> comenzi = query.getResultList();

        if (!comenzi.isEmpty()) {
            em.createQuery("SELECT DISTINCT c FROM ComandaAprovizionare c LEFT JOIN FETCH c.linii WHERE c IN :comenzi", ComandaAprovizionare.class)
              .setParameter("comenzi", comenzi)
              .getResultList();
            em.createQuery("SELECT DISTINCT c FROM ComandaAprovizionare c LEFT JOIN FETCH c.loturi WHERE c IN :comenzi", ComandaAprovizionare.class)
              .setParameter("comenzi", comenzi)
              .getResultList();
        }
        return comenzi;
    }

    @Override
    public List<ComandaAprovizionare> findAllWithLiniiAndLoturi(IdFurnizor furnizorId) {
        StringBuilder jpql = new StringBuilder("SELECT DISTINCT c FROM ComandaAprovizionare c LEFT JOIN FETCH c.linii");
        if (furnizorId != null) {
            jpql.append(" WHERE c.furnizorId = :fId");
        }
        jpql.append(" ORDER BY c.dataComanda DESC");

        var query = em.createQuery(jpql.toString(), ComandaAprovizionare.class);
        if (furnizorId != null) {
            query.setParameter("fId", furnizorId);
        }
        List<ComandaAprovizionare> comenzi = query.getResultList();

        if (!comenzi.isEmpty()) {
            em.createQuery("SELECT DISTINCT c FROM ComandaAprovizionare c LEFT JOIN FETCH c.loturi WHERE c IN :comenzi", ComandaAprovizionare.class)
              .setParameter("comenzi", comenzi)
              .getResultList();
        }
        return comenzi;
    }

    @Override
    public Optional<ComandaAprovizionare> findByCodLot(String codLot) {
        if (codLot == null || codLot.isBlank()) {
            return Optional.empty();
        }
        List<ComandaAprovizionare> result = em.createQuery(
                "SELECT DISTINCT c FROM ComandaAprovizionare c JOIN c.loturi l WHERE LOWER(l.codLot) = LOWER(:codLot)",
                ComandaAprovizionare.class)
                .setParameter("codLot", codLot.trim())
                .setMaxResults(1)
                .getResultList();
        return result.isEmpty() ? Optional.empty() : Optional.of(result.get(0));
    }

    @Override
    public List<ComandaAprovizionare> findComenziNeconfirmateInainteDe(StareComanda stare, java.time.LocalDate dataLimita) {
        return em.createQuery(
                "SELECT c FROM ComandaAprovizionare c WHERE c.stareComanda = :stare AND c.dataComanda < :dataLimita",
                ComandaAprovizionare.class)
                .setParameter("stare", stare)
                .setParameter("dataLimita", dataLimita)
                .getResultList();
    }
}
