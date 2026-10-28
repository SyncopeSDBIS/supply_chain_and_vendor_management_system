package ro.uaic.feaa.sdbis.supplychain.comenzi.application.query;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ro.uaic.feaa.sdbis.supplychain.comenzi.domain.*;
import ro.uaic.feaa.sdbis.supplychain.shared.IdComanda;
import ro.uaic.feaa.sdbis.supplychain.shared.IdFurnizor;
import ro.uaic.feaa.sdbis.supplychain.shared.Perioada;
import ro.uaic.feaa.sdbis.supplychain.shared.annotation.QueryService;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
@Transactional(readOnly = true)
@QueryService
public class ComandaQueryService {

    private final ComandaRepo comandaRepo;
    private final LotRepo lotRepo;

    public ComandaQueryService(ComandaRepo comandaRepo, LotRepo lotRepo) {
        this.comandaRepo = comandaRepo;
        this.lotRepo = lotRepo;
    }

    public List<DashboardLivrariView> obtineDashboardLivrari(UUID furnizorId) {
        List<ComandaAprovizionare> comenzi = comandaRepo.findAll();
        List<DashboardLivrariView> views = new ArrayList<>();

        for (ComandaAprovizionare c : comenzi) {
            if (furnizorId != null && !c.getFurnizorId().getValue().equals(furnizorId)) {
                continue;
            }

            BigDecimal totalComandat = c.getLinii().stream()
                .map(l -> l.getCantitate().getValoare())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal totalPrimit = c.getLinii().stream()
                .map(l -> c.calculeazaCantitatePrimita(l.getMaterial()).getValoare())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal totalRamas = totalComandat.subtract(totalPrimit);
            if (totalRamas.compareTo(BigDecimal.ZERO) < 0) totalRamas = BigDecimal.ZERO;

            LocalDate estimata = c.getEstimare() != null ? c.getEstimare().getDataEstimata() : null;
            LocalDate promis = c.getDataLivrarePromisa();
            LocalDate ceruta = c.getDataLivrareCeruta();

            int zileIntarziere = 0;
            if (promis != null && ceruta != null && promis.isAfter(ceruta)) {
                zileIntarziere = (int) ChronoUnit.DAYS.between(ceruta, promis);
            }

            boolean otif = c.esteAcoperitaIntegral() && zileIntarziere == 0;

            views.add(new DashboardLivrariView(
                c.getId().getValue(),
                c.getNumarComanda(),
                c.getFurnizorId().getValue(),
                c.getStareComanda(),
                c.getDataComanda(),
                ceruta,
                promis,
                estimata,
                c.getStareComanda() == StareComanda.Receptionat ? LocalDate.now() : null,
                totalComandat,
                totalPrimit,
                totalRamas,
                zileIntarziere,
                otif
            ));
        }
        return views;
    }

    public Optional<TrasabilitateLotView> obtineTrasabilitateLot(String codLot) {
        List<ComandaAprovizionare> comenzi = comandaRepo.findAll();
        for (ComandaAprovizionare c : comenzi) {
            for (Lot l : c.getLoturi()) {
                if (l.getCodLot().equalsIgnoreCase(codLot)) {
                    String awb = c.getExpedieri().isEmpty() ? "N/A" : c.getExpedieri().get(c.getExpedieri().size() - 1).getNrAWB();
                    return Optional.of(new TrasabilitateLotView(
                        l.getIdLot().getValue(),
                        l.getCodLot(),
                        l.getMaterial().getCod(),
                        l.getCantitate().getValoare(),
                        l.getCantitatePrimita().getValoare(),
                        l.getDataProductie(),
                        l.getDataExpirare(),
                        l.getStareLot(),
                        c.getId().getValue(),
                        c.getNumarComanda(),
                        c.getFurnizorId().getValue(),
                        c.getDepozit(),
                        awb
                    ));
                }
            }
        }
        return Optional.empty();
    }

    public IndicatoriOTIFFurnizorView calculeazaIndicatoriOTIF(IdFurnizor furnizorId, Perioada perioada) {
        List<ComandaAprovizionare> comenzi = comandaRepo.findAll();
        int total = 0;
        int otifCount = 0;

        for (ComandaAprovizionare c : comenzi) {
            if (c.getFurnizorId().equals(furnizorId)) {
                total++;
                if (c.esteAcoperitaIntegral()) {
                    otifCount++;
                }
            }
        }

        BigDecimal rataOTIF = total == 0 ? BigDecimal.valueOf(100.0) :
            BigDecimal.valueOf(otifCount).multiply(BigDecimal.valueOf(100)).divide(BigDecimal.valueOf(total), 2, RoundingMode.HALF_UP);

        return new IndicatoriOTIFFurnizorView(
            furnizorId.getValue(),
            perioada,
            total,
            otifCount,
            rataOTIF,
            BigDecimal.valueOf(98.5), // pret conformitate
            BigDecimal.valueOf(12.0)  // lead time mediu
        );
    }
}
