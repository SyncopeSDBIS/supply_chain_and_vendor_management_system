package ro.uaic.feaa.sdbis.supplychain.facturi.infrastructure;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import ro.uaic.feaa.sdbis.supplychain.comenzi.api.IComenziModuleApi;
import ro.uaic.feaa.sdbis.supplychain.comenzi.api.dto.ComandaSummaryDTO;
import ro.uaic.feaa.sdbis.supplychain.facturi.domain.ComandaSummaryVO;
import ro.uaic.feaa.sdbis.supplychain.facturi.domain.IComenziAdapter;
import ro.uaic.feaa.sdbis.supplychain.facturi.domain.LinieSumar;
import ro.uaic.feaa.sdbis.supplychain.shared.CodMaterial;
import ro.uaic.feaa.sdbis.supplychain.shared.IdComanda;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Anti-Corruption Layer (ACL) Adapter (Figura 13 si 21).
 * Consuma contractul public IComenziModuleApi si traduce in ComandaSummaryVO conform regulilor Modular Monolith.
 */
@Component
public class ComenziAdapterRest implements IComenziAdapter {

    private final IComenziModuleApi comenziApi;

    public ComenziAdapterRest(@Lazy IComenziModuleApi comenziApi) {
        this.comenziApi = comenziApi;
    }

    @Override
    public ComandaSummaryVO obtineDateComanda(IdComanda id) {
        ComandaSummaryDTO dto = comenziApi.obtineDetaliiComanda(id.getValue())
                .orElseThrow(() -> new IllegalArgumentException("Comanda nu a fost gasita: " + id));

        List<LinieSumar> linii = dto.linii().stream()
            .map(l -> new LinieSumar(CodMaterial.of(l.material()), l.cantitate(), l.pretUnitar()))
            .collect(Collectors.toList());
        return new ComandaSummaryVO(IdComanda.of(dto.idComanda()), linii);
    }
}
