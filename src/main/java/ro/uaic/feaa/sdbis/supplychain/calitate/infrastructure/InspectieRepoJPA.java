package ro.uaic.feaa.sdbis.supplychain.calitate.infrastructure;

import org.springframework.stereotype.Repository;
import ro.uaic.feaa.sdbis.supplychain.calitate.domain.InspectieCalitate;
import ro.uaic.feaa.sdbis.supplychain.calitate.domain.InspectieRepo;
import ro.uaic.feaa.sdbis.supplychain.calitate.domain.StareInspectie;
import ro.uaic.feaa.sdbis.supplychain.calitate.domain.Verdict;
import ro.uaic.feaa.sdbis.supplychain.shared.AbstractRepositoryJPA;
import ro.uaic.feaa.sdbis.supplychain.shared.IdFurnizor;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public class InspectieRepoJPA extends AbstractRepositoryJPA<InspectieCalitate, UUID> implements InspectieRepo {
    public InspectieRepoJPA() {
        super(InspectieCalitate.class);
    }

    @Override
    public List<InspectieCalitate> findInspectii(IdFurnizor furnizorId, UUID inspectorId, StareInspectie stare, int page, int size) {
        StringBuilder jpql = new StringBuilder("SELECT i FROM InspectieCalitate i WHERE 1=1");
        if (furnizorId != null) {
            jpql.append(" AND i.furnizorId = :fId");
        }
        if (inspectorId != null) {
            jpql.append(" AND i.inspectorId = :inspId");
        }
        if (stare != null) {
            jpql.append(" AND i.stareInspectie = :stare");
        }
        jpql.append(" ORDER BY i.perioadaPlanificata.dataInceput DESC");

        var query = em.createQuery(jpql.toString(), InspectieCalitate.class);
        if (furnizorId != null) {
            query.setParameter("fId", furnizorId);
        }
        if (inspectorId != null) {
            query.setParameter("inspId", inspectorId);
        }
        if (stare != null) {
            query.setParameter("stare", stare);
        }
        query.setFirstResult(Math.max(0, page) * Math.max(1, size));
        query.setMaxResults(Math.max(1, size));
        return query.getResultList();
    }

    @Override
    public BigDecimal calculeazaRataRespingere(IdFurnizor furnizorId, LocalDate deLa, LocalDate panaLa) {
        Object[] result = (Object[]) em.createQuery(
            "SELECT COUNT(i), SUM(CASE WHEN i.verdict = :neconforma THEN 1L ELSE 0L END) " +
            "FROM InspectieCalitate i " +
            "WHERE i.furnizorId = :fId " +
            "AND i.perioadaPlanificata.dataInceput >= :deLa " +
            "AND i.perioadaPlanificata.dataFinalizare <= :panaLa"
        )
        .setParameter("fId", furnizorId)
        .setParameter("neconforma", Verdict.Neconforma)
        .setParameter("deLa", deLa)
        .setParameter("panaLa", panaLa)
        .getSingleResult();

        long total = result != null && result[0] != null ? ((Number) result[0]).longValue() : 0L;
        long neconforme = result != null && result[1] != null ? ((Number) result[1]).longValue() : 0L;

        if (total == 0) {
            return BigDecimal.ZERO;
        }
        return BigDecimal.valueOf(neconforme)
            .multiply(BigDecimal.valueOf(100))
            .divide(BigDecimal.valueOf(total), 2, RoundingMode.HALF_UP);
    }

    @Override
    public List<InspectieCalitate> findByFurnizorIds(List<IdFurnizor> furnizorIds) {
        if (furnizorIds == null || furnizorIds.isEmpty()) {
            return java.util.Collections.emptyList();
        }
        return em.createQuery("SELECT i FROM InspectieCalitate i WHERE i.furnizorId IN (:fIds)", InspectieCalitate.class)
            .setParameter("fIds", furnizorIds)
            .getResultList();
    }

    @Override
    public long countTotal() {
        Long count = em.createQuery("SELECT COUNT(i) FROM InspectieCalitate i", Long.class).getSingleResult();
        return count != null ? count : 0L;
    }

    @Override
    public long countByVerdict(Verdict verdict) {
        Long count = em.createQuery("SELECT COUNT(i) FROM InspectieCalitate i WHERE i.verdict = :verdict", Long.class)
                .setParameter("verdict", verdict)
                .getSingleResult();
        return count != null ? count : 0L;
    }
}
