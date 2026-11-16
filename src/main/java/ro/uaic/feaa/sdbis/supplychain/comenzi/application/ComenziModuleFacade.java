package ro.uaic.feaa.sdbis.supplychain.comenzi.application;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ro.uaic.feaa.sdbis.supplychain.comenzi.api.IComenziModuleApi;
import ro.uaic.feaa.sdbis.supplychain.comenzi.api.command.CreeazaComandaPublicCommand;
import ro.uaic.feaa.sdbis.supplychain.comenzi.api.dto.ComandaSummaryDTO;
import ro.uaic.feaa.sdbis.supplychain.comenzi.api.dto.LinieComandaDTO;
import ro.uaic.feaa.sdbis.supplychain.comenzi.api.dto.PerformantaLivrariDTO;
import ro.uaic.feaa.sdbis.supplychain.comenzi.application.command.AnuleazaComandaCommand;
import ro.uaic.feaa.sdbis.supplychain.comenzi.application.command.ConfirmaComandaCommand;
import ro.uaic.feaa.sdbis.supplychain.comenzi.application.command.CreeazaComandaCommand;
import ro.uaic.feaa.sdbis.supplychain.comenzi.domain.Cantitate;
import ro.uaic.feaa.sdbis.supplychain.comenzi.domain.ComandaAprovizionare;
import ro.uaic.feaa.sdbis.supplychain.comenzi.domain.ComandaRepo;
import ro.uaic.feaa.sdbis.supplychain.comenzi.domain.LinieComanda;
import ro.uaic.feaa.sdbis.supplychain.comenzi.domain.PretUnitar;
import ro.uaic.feaa.sdbis.supplychain.comenzi.domain.StareComanda;
import ro.uaic.feaa.sdbis.supplychain.comenzi.domain.event.ComandaNeconfirmataEvent;
import ro.uaic.feaa.sdbis.supplychain.shared.CodMaterial;
import ro.uaic.feaa.sdbis.supplychain.shared.IdComanda;
import ro.uaic.feaa.sdbis.supplychain.shared.IdFurnizor;
import ro.uaic.feaa.sdbis.supplychain.shared.OutboxRepo;
import ro.uaic.feaa.sdbis.supplychain.shared.Perioada;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Enterprise Facade implementing the public IComenziModuleApi contract.
 * Bridges external module calls to internal domain aggregates and services.
 */
@Component
@Transactional
public class ComenziModuleFacade implements IComenziModuleApi {

    private final ComandaApplicationService comandaAppService;
    private final ComandaRepo comandaRepo;
    private final OutboxRepo outboxRepo;

    public ComenziModuleFacade(ComandaApplicationService comandaAppService,
                               ComandaRepo comandaRepo,
                               OutboxRepo outboxRepo) {
        this.comandaAppService = comandaAppService;
        this.comandaRepo = comandaRepo;
        this.outboxRepo = outboxRepo;
    }

    @Override
    public int proceseazaTimeoutConfirmareComenzi() {
        LocalDate acum2Zile = LocalDate.now().minusDays(2);
        List<ComandaAprovizionare> comenzi = comandaRepo.findComenziNeconfirmateInainteDe(StareComanda.Solicitat, acum2Zile);
        int count = 0;
        for (ComandaAprovizionare c : comenzi) {
            outboxRepo.saveEvent(new ComandaNeconfirmataEvent(UUID.randomUUID(), Instant.now(), c.getId()));
            count++;
        }
        return count;
    }

    @Override
    public UUID creeazaComanda(CreeazaComandaPublicCommand cmd) {
        List<LinieComanda> linii = cmd.linii().stream()
                .map(l -> new LinieComanda(
                        CodMaterial.of(l.material()),
                        Cantitate.of(l.cantitate(), l.unitateMasura()),
                        PretUnitar.of(l.pretUnitar(), l.moneda())))
                .toList();

        String numar = "CMD-" + System.currentTimeMillis();
        CreeazaComandaCommand internalCmd = new CreeazaComandaCommand(
                numar,
                IdFurnizor.of(cmd.furnizorId()),
                cmd.gestionarId(),
                LocalDate.now(),
                cmd.dataLivrareEstimata(),
                cmd.depozitDestinatie(),
                linii);

        IdComanda id = comandaAppService.creeazaComanda(internalCmd);
        return id.getValue();
    }

    @Override
    public void confirmaComanda(UUID comandaId, LocalDate dataLivrarePromisa) {
        comandaAppService.confirmaComanda(new ConfirmaComandaCommand(IdComanda.of(comandaId), dataLivrarePromisa));
    }

    @Override
    public void anuleazaComanda(UUID comandaId, String motiv) {
        comandaAppService.anuleazaComanda(new AnuleazaComandaCommand(IdComanda.of(comandaId), motiv));
    }

    @Override
    @Transactional(readOnly = true)
    public boolean esteComandaInStare(UUID comandaId, String stare) {
        return comandaRepo.findById(IdComanda.of(comandaId))
                .map(c -> c.getStareComanda().name().equalsIgnoreCase(stare))
                .orElse(false);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean esteComandaEligibilaPentruReconciliere(UUID comandaId) {
        return comandaRepo.findById(IdComanda.of(comandaId))
                .map(c -> c.getStareComanda() == StareComanda.Receptionat || c.getStareComanda() == StareComanda.Inchis)
                .orElse(false);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ComandaSummaryDTO> obtineDetaliiComanda(UUID comandaId) {
        return comandaRepo.findById(IdComanda.of(comandaId))
                .map(this::mapToDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public PerformantaLivrariDTO obtinePerformantaLivrari(UUID furnizorId, LocalDate dataInceput,
            LocalDate dataFinalizare) {
        Perioada perioada = new Perioada(dataInceput, dataFinalizare);
        List<ComandaAprovizionare> comenzi = comandaRepo.findByFurnizorId(IdFurnizor.of(furnizorId)).stream()
                .filter(c -> c.getDataComanda() != null && perioada.includeData(c.getDataComanda()))
                .toList();

        int total = 0;
        int otifCount = 0;
        int pretConformCount = 0;

        for (ComandaAprovizionare c : comenzi) {
            if (c.getStareComanda() != StareComanda.Receptionat && c.getStareComanda() != StareComanda.Inchis) {
                continue;
            }
            total++;
            boolean livratLaTimp = c.getDataLivrarePromisa() == null
                    || !c.getDataLivrarePromisa().isAfter(c.getDataLivrareCeruta());
            if (c.esteAcoperitaIntegral() && livratLaTimp) {
                otifCount++;
            }
            if (c.getStareComanda() != StareComanda.Refuzat
                    && c.calculeazaTotal().compareTo(BigDecimal.ZERO) > 0) {
                pretConformCount++;
            }
        }

        BigDecimal otif = total == 0
                ? BigDecimal.valueOf(100.0)
                : BigDecimal.valueOf(otifCount)
                        .multiply(BigDecimal.valueOf(100))
                        .divide(BigDecimal.valueOf(total), 2, RoundingMode.HALF_UP);

        BigDecimal conformitatePret = total == 0
                ? BigDecimal.valueOf(100.0)
                : BigDecimal.valueOf(pretConformCount)
                        .multiply(BigDecimal.valueOf(100))
                        .divide(BigDecimal.valueOf(total), 2, RoundingMode.HALF_UP);
        return new PerformantaLivrariDTO(furnizorId, otif, conformitatePret, total, otifCount);
    }

    private ComandaSummaryDTO mapToDTO(ComandaAprovizionare c) {
        List<LinieComandaDTO> linii = c.getLinii().stream()
                .map(l -> new LinieComandaDTO(
                        l.getMaterial().getCod(),
                        l.getCantitate().getValoare(),
                        l.getCantitate().getUnitateMasura(),
                        l.getPretUnitar().getValoare(),
                        l.getPretUnitar().getMoneda().getCurrencyCode()))
                .toList();

        return new ComandaSummaryDTO(
                c.getId().getValue(),
                c.getNumarComanda(),
                c.getFurnizorId().getValue(),
                c.getStareComanda().name(),
                c.getDataLivrareCeruta(),
                linii);
    }
}
