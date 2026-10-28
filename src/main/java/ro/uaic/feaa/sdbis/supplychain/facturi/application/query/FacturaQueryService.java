package ro.uaic.feaa.sdbis.supplychain.facturi.application.query;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ro.uaic.feaa.sdbis.supplychain.facturi.domain.*;
import ro.uaic.feaa.sdbis.supplychain.shared.annotation.QueryService;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
@QueryService
public class FacturaQueryService {

    private final FacturaRepo facturaRepo;
    private final FurnizoriBlocatiRepo furnizoriBlocatiRepo;

    public FacturaQueryService(FacturaRepo facturaRepo, FurnizoriBlocatiRepo furnizoriBlocatiRepo) {
        this.facturaRepo = facturaRepo;
        this.furnizoriBlocatiRepo = furnizoriBlocatiRepo;
    }

    public List<ScadentarFacturiView> obtineScadentar() {
        List<FacturaFurnizor> facturi = facturaRepo.findAll();
        List<ScadentarFacturiView> views = new ArrayList<>();
        LocalDate azi = LocalDate.now();

        for (FacturaFurnizor f : facturi) {
            long zilePanaLaScadenta = ChronoUnit.DAYS.between(azi, f.getDataScadenta());
            boolean blocat = furnizoriBlocatiRepo.esteBlocat(f.getFurnizorId());
            boolean alerta = zilePanaLaScadenta <= 3 && f.calculeazaRestDePlata().getValoare().signum() > 0;

            views.add(new ScadentarFacturiView(
                f.getIdFactura(),
                f.getSerie() + "-" + f.getNumar(),
                f.getFurnizorId().getValue(),
                f.getComandaId().getValue(),
                f.getDataEmitere(),
                f.getDataScadenta(),
                zilePanaLaScadenta,
                f.getValoare().getValoareTotala(),
                f.getSumaPlatita().getValoare(),
                f.calculeazaRestDePlata().getValoare(),
                f.getStareFactura(),
                blocat,
                alerta
            ));
        }
        return views;
    }
}
