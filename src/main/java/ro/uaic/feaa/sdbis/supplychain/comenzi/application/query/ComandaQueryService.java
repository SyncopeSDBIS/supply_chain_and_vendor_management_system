package ro.uaic.feaa.sdbis.supplychain.comenzi.application.query;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ro.uaic.feaa.sdbis.supplychain.comenzi.domain.ComandaAprovizionare;
import ro.uaic.feaa.sdbis.supplychain.comenzi.domain.ComandaRepo;
import ro.uaic.feaa.sdbis.supplychain.comenzi.domain.Lot;
import ro.uaic.feaa.sdbis.supplychain.comenzi.domain.StareComanda;
import ro.uaic.feaa.sdbis.supplychain.shared.IdComanda;
import ro.uaic.feaa.sdbis.supplychain.shared.IdFurnizor;
import ro.uaic.feaa.sdbis.supplychain.shared.IdLot;
import ro.uaic.feaa.sdbis.supplychain.shared.Perioada;
import ro.uaic.feaa.sdbis.supplychain.shared.annotation.QueryService;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@QueryService
public class ComandaQueryService {

    private final ComandaRepo comandaRepo;

    public ComandaQueryService(ComandaRepo comandaRepo) {
        this.comandaRepo = comandaRepo;
    }

    public List<DashboardLivrariView> obtineDashboardLivrari(UUID furnizorId) {
        return obtineDashboardLivrari(furnizorId, 0, 100);
    }

    public List<DashboardLivrariView> obtineDashboardLivrari(UUID furnizorId, int page, int size) {
        IdFurnizor fId = furnizorId != null ? IdFurnizor.of(furnizorId) : null;
        // FIX JPA-02: Paginare optimizata cu two-step fetch pentru a preveni Heap Exhaustion
        List<ComandaAprovizionare> comenzi = comandaRepo.findComenzi(fId, null, page, size);
        List<DashboardLivrariView> views = new ArrayList<>(comenzi.size());

        for (ComandaAprovizionare c : comenzi) {
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
        return comandaRepo.findByCodLot(codLot).flatMap(c -> {
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
            return Optional.empty();
        });
    }

    public IndicatoriOTIFFurnizorView calculeazaIndicatoriOTIF(IdFurnizor furnizorId, Perioada perioada) {
        List<ComandaAprovizionare> comenzi = comandaRepo.findAllWithLiniiAndLoturi(furnizorId);
        int total = 0;
        int otifCount = 0;

        for (ComandaAprovizionare c : comenzi) {
            if (perioada != null && (c.getDataComanda().isBefore(perioada.getDataInceput()) || c.getDataComanda().isAfter(perioada.getDataFinalizare()))) {
                continue;
            }
            total++;
            boolean livratLaTimp = c.getDataLivrarePromisa() == null || !c.getDataLivrarePromisa().isAfter(c.getDataLivrareCeruta());
            if (c.esteAcoperitaIntegral() && livratLaTimp) {
                otifCount++;
            }
        }

        BigDecimal rataOTIF = total == 0 ? BigDecimal.valueOf(100.0) :
            BigDecimal.valueOf(otifCount).multiply(BigDecimal.valueOf(100)).divide(BigDecimal.valueOf(total), 2, RoundingMode.HALF_UP);

        double avgLeadTime = comenzi.stream()
            .filter(c -> c.getDataComanda() != null && c.getDataLivrarePromisa() != null)
            .mapToLong(c -> java.time.temporal.ChronoUnit.DAYS.between(c.getDataComanda(), c.getDataLivrarePromisa()))
            .filter(d -> d > 0)
            .average()
            .orElse(0.0);
        BigDecimal leadTimeMediu = BigDecimal.valueOf(avgLeadTime).setScale(1, RoundingMode.HALF_UP);
        BigDecimal pretConformitate = BigDecimal.valueOf(100.0);

        return new IndicatoriOTIFFurnizorView(
            furnizorId.getValue(),
            perioada,
            total,
            otifCount,
            rataOTIF,
            pretConformitate,
            leadTimeMediu
        );
    }

    public List<ComandaListItemView> listeazaComenzi(UUID furnizorId, String stare, int page, int size) {
        StareComanda stareEnum = null;
        if (stare != null && !stare.isBlank()) {
            stareEnum = StareComanda.valueOf(stare.trim());
        }
        IdFurnizor fId = furnizorId != null ? IdFurnizor.of(furnizorId) : null;
        List<ComandaAprovizionare> comenzi = comandaRepo.findComenzi(fId, stareEnum, page, size);
        return comenzi.stream()
            .map(c -> {
                BigDecimal totalValoare = c.calculeazaTotal();
                return new ComandaListItemView(
                    c.getId().getValue(),
                    c.getNumarComanda(),
                    c.getFurnizorId().getValue(),
                    c.getCumparatorId(),
                    c.getDataComanda(),
                    c.getDataLivrareCeruta(),
                    c.getDataLivrarePromisa(),
                    c.getDepozit(),
                    c.getStareComanda(),
                    c.getLinii().size(),
                    c.getLoturi().size(),
                    totalValoare
                );
            })
            .collect(Collectors.toList());
    }

    public Optional<ComandaDetaliiView> obtineDetaliiComanda(IdComanda id) {
        return comandaRepo.findById(id).map(c -> {
            var linii = c.getLinii().stream()
                .map(l -> new ComandaDetaliiView.LinieDetaliiItemView(
                    l.getMaterial().getCod(),
                    l.getCantitate().getValoare(),
                    l.getCantitate().getUnitateMasura(),
                    l.getPretUnitar().getValoare(),
                    l.getPretUnitar().getMoneda().getCurrencyCode()
                ))
                .toList();

            var loturi = c.getLoturi().stream()
                .map(l -> new ComandaDetaliiView.LotDetaliiItemView(
                    l.getIdLot().getValue(),
                    l.getCodLot(),
                    l.getMaterial().getCod(),
                    l.getCantitate().getValoare(),
                    l.getCantitatePrimita().getValoare(),
                    l.getCantitate().getUnitateMasura(),
                    l.getDataProductie(),
                    l.getDataExpirare(),
                    l.getStareLot().name()
                ))
                .toList();

            var expedieri = c.getExpedieri().stream()
                .map(e -> new ComandaDetaliiView.ExpediereDetaliiItemView(
                    e.getTransportator(),
                    e.getNrAWB(),
                    e.getDataExpediere(),
                    e.getLoturi().stream().map(IdLot::getValue).map(UUID::toString).toList()
                ))
                .toList();

            return new ComandaDetaliiView(
                c.getId().getValue(),
                c.getNumarComanda(),
                c.getFurnizorId().getValue(),
                c.getCumparatorId(),
                c.getDataComanda(),
                c.getDataLivrareCeruta(),
                c.getDataLivrarePromisa(),
                c.getDepozit(),
                c.getStareComanda(),
                linii,
                loturi,
                expedieri
            );
        });
    }
}
