package ro.uaic.feaa.sdbis.supplychain.comenzi.infrastructure;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import ro.uaic.feaa.sdbis.supplychain.calitate.api.ICalitateModuleApi;
import ro.uaic.feaa.sdbis.supplychain.comenzi.domain.IGestiuneFurnizoriAdapter;
import ro.uaic.feaa.sdbis.supplychain.shared.IdFurnizor;

/**
 * Adapter din Comenzi catre Calitate (Figura 11).
 * Consuma contractul public ICalitateModuleApi conform regulilor Modular Monolith.
 */
@Component
public class GestiuneFurnizoriAdapterRest implements IGestiuneFurnizoriAdapter {

    private final ICalitateModuleApi calitateApi;

    public GestiuneFurnizoriAdapterRest(@Lazy ICalitateModuleApi calitateApi) {
        this.calitateApi = calitateApi;
    }

    @Override
    public boolean esteFurnizorEligibil(IdFurnizor id) {
        return calitateApi.esteFurnizorEligibilPentruComenzi(id.getValue());
    }
}
