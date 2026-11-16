package ro.uaic.feaa.sdbis.supplychain.comenzi.infrastructure;

import org.springframework.stereotype.Component;
import ro.uaic.feaa.sdbis.supplychain.comenzi.domain.ComandaAprovizionare;
import ro.uaic.feaa.sdbis.supplychain.comenzi.domain.ComandaRepo;
import ro.uaic.feaa.sdbis.supplychain.shared.IFurnizorComandaAdapter;
import ro.uaic.feaa.sdbis.supplychain.shared.IPerformantaLivrariAdapter;
import ro.uaic.feaa.sdbis.supplychain.shared.IdComanda;
import ro.uaic.feaa.sdbis.supplychain.shared.IdFurnizor;
import ro.uaic.feaa.sdbis.supplychain.shared.Perioada;
import ro.uaic.feaa.sdbis.supplychain.shared.PerformantaLivrariVO;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Enterprise Adapter in modulul Comenzi ce furnizeaza metrici reale de performanta livrari (OTIF)
 * si asocierea furnizorilor la comenzi pentru evaluarea calitatii.
 * Decupleaza complet Bounded Context-ul Calitate de Comenzi (ADR-002 Inversion of Control).
 */
@Component
public class ComenziPerformantaLivrariAdapter implements IPerformantaLivrariAdapter, IFurnizorComandaAdapter {

    private final ComandaRepo comandaRepo;

    public ComenziPerformantaLivrariAdapter(ComandaRepo comandaRepo) {
        this.comandaRepo = comandaRepo;
    }

    @Override
    public PerformantaLivrariVO obtinePerformanta(IdFurnizor furnizorId, Perioada luna) {
        List<ComandaAprovizionare> comenzi = comandaRepo.findAllWithLiniiAndLoturi(furnizorId);
        int total = 0;
        int otifCount = 0;

        LocalDate start = luna != null ? luna.getDataInceput() : null;
        LocalDate end = luna != null ? luna.getDataFinalizare() : null;

        for (ComandaAprovizionare c : comenzi) {
            LocalDate dt = c.getDataComanda();
            if (start != null && dt.isBefore(start)) {
                continue;
            }
            if (end != null && dt.isAfter(end)) {
                continue;
            }
            total++;
            boolean livratLaTimp = c.getDataLivrarePromisa() == null || !c.getDataLivrarePromisa().isAfter(c.getDataLivrareCeruta());
            if (c.esteAcoperitaIntegral() && livratLaTimp) {
                otifCount++;
            }
        }

        BigDecimal otif = total == 0 
                ? BigDecimal.valueOf(100.0) 
                : BigDecimal.valueOf(otifCount)
                    .multiply(BigDecimal.valueOf(100))
                    .divide(BigDecimal.valueOf(total), 2, RoundingMode.HALF_UP);

        BigDecimal conformitatePret = BigDecimal.valueOf(100.0);
        return new PerformantaLivrariVO(furnizorId, luna, otif, conformitatePret);
    }

    @Override
    public Optional<IdFurnizor> gasesteFurnizorComanda(IdComanda comandaId) {
        if (comandaId == null) {
            return Optional.empty();
        }
        return comandaRepo.findById(comandaId).map(ComandaAprovizionare::getFurnizorId);
    }
}

