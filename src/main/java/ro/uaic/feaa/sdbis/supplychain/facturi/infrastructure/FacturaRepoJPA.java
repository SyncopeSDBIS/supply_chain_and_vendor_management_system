package ro.uaic.feaa.sdbis.supplychain.facturi.infrastructure;

import org.springframework.stereotype.Repository;
import ro.uaic.feaa.sdbis.supplychain.facturi.domain.FacturaFurnizor;
import ro.uaic.feaa.sdbis.supplychain.facturi.domain.FacturaRepo;
import ro.uaic.feaa.sdbis.supplychain.facturi.domain.StareFactura;
import ro.uaic.feaa.sdbis.supplychain.shared.AbstractRepositoryJPA;
import ro.uaic.feaa.sdbis.supplychain.shared.IdComanda;
import ro.uaic.feaa.sdbis.supplychain.shared.IdFurnizor;

import java.util.List;
import java.util.UUID;

@Repository
public class FacturaRepoJPA extends AbstractRepositoryJPA<FacturaFurnizor, UUID> implements FacturaRepo {
    public FacturaRepoJPA() {
        super(FacturaFurnizor.class);
    }

    @Override
    public List<FacturaFurnizor> findByComandaId(IdComanda comandaId) {
        return em.createQuery("SELECT f FROM FacturaFurnizor f WHERE f.comandaId = :cid", FacturaFurnizor.class)
                 .setParameter("cid", comandaId)
                 .getResultList();
    }

    @Override
    public List<FacturaFurnizor> findFacturi(IdFurnizor furnizorId, IdComanda comandaId, StareFactura stare, int page, int size) {
        StringBuilder jpql = new StringBuilder("SELECT f FROM FacturaFurnizor f WHERE 1=1");
        if (furnizorId != null) {
            jpql.append(" AND f.furnizorId = :fId");
        }
        if (comandaId != null) {
            jpql.append(" AND f.comandaId = :cId");
        }
        if (stare != null) {
            jpql.append(" AND f.stareFactura = :stare");
        }
        jpql.append(" ORDER BY f.dataScadenta ASC");

        var query = em.createQuery(jpql.toString(), FacturaFurnizor.class);
        if (furnizorId != null) {
            query.setParameter("fId", furnizorId);
        }
        if (comandaId != null) {
            query.setParameter("cId", comandaId);
        }
        if (stare != null) {
            query.setParameter("stare", stare);
        }
        query.setFirstResult(Math.max(0, page) * Math.max(1, size));
        query.setMaxResults(Math.max(1, size));
        List<FacturaFurnizor> facturi = query.getResultList();
        if (!facturi.isEmpty()) {
            em.createQuery("SELECT DISTINCT f FROM FacturaFurnizor f LEFT JOIN FETCH f.linii WHERE f IN :facturi", FacturaFurnizor.class)
              .setParameter("facturi", facturi)
              .getResultList();
        }
        return facturi;
    }

    @Override
    public List<FacturaFurnizor> findFacturiNeachitate() {
        return findFacturiNeachitate(200);
    }

    @Override
    public List<FacturaFurnizor> findFacturiNeachitate(int maxResults) {
        var query = em.createQuery(
            "SELECT f FROM FacturaFurnizor f WHERE f.stareFactura NOT IN (:excluse) ORDER BY f.dataScadenta ASC",
            FacturaFurnizor.class
        )
        .setParameter("excluse", List.of(StareFactura.Platita, StareFactura.Anulata));

        if (maxResults > 0) {
            query.setMaxResults(maxResults);
        }
        return query.getResultList();
    }

    @Override
    public List<FacturaFurnizor> findFacturiCuScadentaLa(List<StareFactura> stari, java.time.LocalDate dataScadenta) {
        return em.createQuery(
            "SELECT f FROM FacturaFurnizor f WHERE f.stareFactura IN (:stari) AND f.dataScadenta = :scadenta",
            FacturaFurnizor.class
        )
        .setParameter("stari", stari)
        .setParameter("scadenta", dataScadenta)
        .getResultList();
    }
}
