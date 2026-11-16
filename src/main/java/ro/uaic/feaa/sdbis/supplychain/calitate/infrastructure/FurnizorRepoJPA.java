package ro.uaic.feaa.sdbis.supplychain.calitate.infrastructure;

import org.springframework.stereotype.Repository;
import ro.uaic.feaa.sdbis.supplychain.calitate.domain.Furnizor;
import ro.uaic.feaa.sdbis.supplychain.calitate.domain.FurnizorRepo;
import ro.uaic.feaa.sdbis.supplychain.calitate.domain.StareFurnizor;
import ro.uaic.feaa.sdbis.supplychain.shared.AbstractRepositoryJPA;
import ro.uaic.feaa.sdbis.supplychain.shared.IdFurnizor;

import java.util.List;

@Repository
public class FurnizorRepoJPA extends AbstractRepositoryJPA<Furnizor, IdFurnizor> implements FurnizorRepo {
    public FurnizorRepoJPA() {
        super(Furnizor.class);
    }

    @Override
    public List<Furnizor> findFurnizori(StareFurnizor stare, String categorie, int page, int size) {
        return findFurnizori(stare, categorie, null, page, size);
    }

    @Override
    public List<Furnizor> findFurnizori(StareFurnizor stare, String categorie, String search, int page, int size) {
        StringBuilder jpql = new StringBuilder("SELECT DISTINCT f FROM Furnizor f");
        boolean hasWhere = false;

        if (categorie != null && !categorie.isBlank()) {
            jpql.append(" JOIN f.categoriiMateriale c WHERE LOWER(c) = LOWER(:cat)");
            hasWhere = true;
        }

        if (stare != null) {
            jpql.append(hasWhere ? " AND f.stareFurnizor = :stare" : " WHERE f.stareFurnizor = :stare");
            hasWhere = true;
        }

        if (search != null && !search.isBlank()) {
            jpql.append(hasWhere ? " AND (LOWER(f.denumire) LIKE LOWER(:search) OR LOWER(f.codFiscal) LIKE LOWER(:search))"
                                 : " WHERE (LOWER(f.denumire) LIKE LOWER(:search) OR LOWER(f.codFiscal) LIKE LOWER(:search))");
        }

        jpql.append(" ORDER BY f.denumire ASC");

        var query = em.createQuery(jpql.toString(), Furnizor.class);
        if (categorie != null && !categorie.isBlank()) {
            query.setParameter("cat", categorie.trim());
        }
        if (stare != null) {
            query.setParameter("stare", stare);
        }
        if (search != null && !search.isBlank()) {
            query.setParameter("search", "%" + search.trim() + "%");
        }

        query.setFirstResult(Math.max(0, page) * Math.max(1, size));
        query.setMaxResults(Math.max(1, size));
        List<Furnizor> furnizori = query.getResultList();

        if (!furnizori.isEmpty()) {
            em.createQuery("SELECT DISTINCT f FROM Furnizor f LEFT JOIN FETCH f.categoriiMateriale WHERE f IN :furnizori", Furnizor.class)
              .setParameter("furnizori", furnizori)
              .getResultList();

            em.createQuery("SELECT DISTINCT f FROM Furnizor f LEFT JOIN FETCH f.scorecarduri WHERE f IN :furnizori", Furnizor.class)
              .setParameter("furnizori", furnizori)
              .getResultList();
        }

        return furnizori;
    }

    @Override
    public List<Furnizor> findEligibiliPentruComenzi() {
        return em.createQuery(
            "SELECT DISTINCT f FROM Furnizor f LEFT JOIN FETCH f.scorecarduri WHERE f.stareFurnizor IN (:stari)",
            Furnizor.class
        )
        .setParameter("stari", List.of(StareFurnizor.Aprobat, StareFurnizor.Conditionat))
        .getResultList();
    }

    @Override
    public long countTotal() {
        Long count = em.createQuery("SELECT COUNT(f) FROM Furnizor f", Long.class).getSingleResult();
        return count != null ? count : 0L;
    }

    @Override
    public long countByStare(StareFurnizor stare) {
        Long count = em.createQuery("SELECT COUNT(f) FROM Furnizor f WHERE f.stareFurnizor = :stare", Long.class)
                .setParameter("stare", stare)
                .getSingleResult();
        return count != null ? count : 0L;
    }
}
