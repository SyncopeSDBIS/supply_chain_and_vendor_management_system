package ro.uaic.feaa.sdbis.supplychain.comenzi.infrastructure;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import ro.uaic.feaa.sdbis.supplychain.calitate.application.CalitateApplicationService;
import ro.uaic.feaa.sdbis.supplychain.comenzi.domain.IGestiuneFurnizoriAdapter;
import ro.uaic.feaa.sdbis.supplychain.shared.IdFurnizor;

/**
 * Adapter din Comenzi catre Calitate (Figura 11).
 * Implementeaza portul IGestiuneFurnizoriAdapter facand apel direct / REST la CalitateApplicationService.
 */
@Component
public class GestiuneFurnizoriAdapterRest implements IGestiuneFurnizoriAdapter {

    private final CalitateApplicationService calitateAppService;

    public GestiuneFurnizoriAdapterRest(@Lazy CalitateApplicationService calitateAppService) {
        this.calitateAppService = calitateAppService;
    }

    @Override
    public boolean esteFurnizorEligibil(IdFurnizor id) {
        return calitateAppService.esteFurnizorEligibil(id);
    }
}
