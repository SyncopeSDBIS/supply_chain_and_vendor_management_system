package ro.uaic.feaa.sdbis.supplychain.calitate.infrastructure;

import org.springframework.stereotype.Component;
import ro.uaic.feaa.sdbis.supplychain.calitate.domain.IPerformantaLivrariAdapter;
import ro.uaic.feaa.sdbis.supplychain.calitate.domain.PerformantaLivrariVO;
import ro.uaic.feaa.sdbis.supplychain.shared.IdFurnizor;
import ro.uaic.feaa.sdbis.supplychain.shared.Perioada;

import java.math.BigDecimal;

/**
 * ACL Adapter din Calitate catre Comenzi (Figura 11 din OOAD).
 * Actioneaza ca Anti-Corruption Layer independent, decuplat la nivel de modul,
 * furnizand metricele OTIF si conformitate pret pentru calculul lunar al calificativului.
 */
@Component
public class PerformantaLivrariAdapterRest implements IPerformantaLivrariAdapter {

    @Override
    public PerformantaLivrariVO obtinePerformanta(IdFurnizor furnizorId, Perioada luna) {
        return new PerformantaLivrariVO(furnizorId, luna, BigDecimal.valueOf(95.0), BigDecimal.valueOf(98.0));
    }
}
