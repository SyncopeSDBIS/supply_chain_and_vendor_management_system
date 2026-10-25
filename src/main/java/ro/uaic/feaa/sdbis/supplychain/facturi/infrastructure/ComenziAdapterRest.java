package ro.uaic.feaa.sdbis.supplychain.facturi.infrastructure;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import ro.uaic.feaa.sdbis.supplychain.comenzi.application.ComandaApplicationService;
import ro.uaic.feaa.sdbis.supplychain.comenzi.application.dto.ComandaSummaryDTO;
import ro.uaic.feaa.sdbis.supplychain.facturi.domain.ComandaSummaryVO;
import ro.uaic.feaa.sdbis.supplychain.facturi.domain.IComenziAdapter;
import ro.uaic.feaa.sdbis.supplychain.facturi.domain.LinieSumar;
import ro.uaic.feaa.sdbis.supplychain.shared.CodMaterial;
import ro.uaic.feaa.sdbis.supplychain.shared.IdComanda;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Anti-Corruption Layer (ACL) Adapter (Figura 13 si 21).
 * Traduce Published Language ComandaSummaryDTO in Value Object-ul domeniului ComandaSummaryVO.
 */
@Component
public class ComenziAdapterRest implements IComenziAdapter {

    private final ComandaApplicationService comandaAppService;

    public ComenziAdapterRest(@Lazy ComandaApplicationService comandaAppService) {
        this.comandaAppService = comandaAppService;
    }

    @Override
    public ComandaSummaryVO obtineDateComanda(IdComanda id) {
        ComandaSummaryDTO dto = comandaAppService.obtineSumarComanda(id);
        List<LinieSumar> linii = dto.getLinii().stream()
            .map(l -> new LinieSumar(CodMaterial.of(l.getMaterial()), l.getCantitateComandata(), l.getPretUnitar()))
            .collect(Collectors.toList());
        return new ComandaSummaryVO(IdComanda.of(dto.getComandaId()), linii);
    }
}
