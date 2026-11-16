package ro.uaic.feaa.sdbis.supplychain.comenzi.infrastructure;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import ro.uaic.feaa.sdbis.supplychain.comenzi.domain.ComandaAprovizionare;
import ro.uaic.feaa.sdbis.supplychain.comenzi.domain.IstoricLivrariRepo;
import ro.uaic.feaa.sdbis.supplychain.comenzi.domain.IstoricLivrariVO;
import ro.uaic.feaa.sdbis.supplychain.comenzi.domain.StareComanda;
import ro.uaic.feaa.sdbis.supplychain.shared.AbstractRepositoryJPA;
import ro.uaic.feaa.sdbis.supplychain.shared.CodMaterial;
import ro.uaic.feaa.sdbis.supplychain.shared.IdFurnizor;

import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Repository
public class IstoricLivrariRepoJPA extends AbstractRepositoryJPA<ComandaAprovizionare, Long>
        implements IstoricLivrariRepo {

    private static final Logger log = LoggerFactory.getLogger(IstoricLivrariRepoJPA.class);

    public IstoricLivrariRepoJPA() {
        super(ComandaAprovizionare.class);
    }

    @Override
    public IstoricLivrariVO obtineIstoric(IdFurnizor furnizorId, CodMaterial material) {
        List<Integer> zileList = new ArrayList<>();
        try {
            List<ComandaAprovizionare> comenzi = em.createQuery(
                    "SELECT c FROM ComandaAprovizionare c WHERE c.furnizorId = :fid AND c.stareComanda = :stare",
                    ComandaAprovizionare.class).setParameter("fid", furnizorId)
                    .setParameter("stare", StareComanda.Receptionat)
                    .setMaxResults(10)
                    .getResultList();

            for (ComandaAprovizionare c : comenzi) {
                if (c.getDataComanda() != null && c.getDataLivrarePromisa() != null) {
                    long zile = ChronoUnit.DAYS.between(c.getDataComanda(), c.getDataLivrarePromisa());
                    if (zile > 0) {
                        zileList.add((int) zile);
                    }
                }
            }
        } catch (Exception ex) {
            log.warn("[IstoricLivrari] Eroare la interogarea istoricului livrarilor pentru furnizorul {}: {}. Se utilizeaza valori implicite.",
                    furnizorId != null ? furnizorId.getValue() : "null", ex.getMessage(), ex);
        }

        return new IstoricLivrariVO(zileList);
    }
}
