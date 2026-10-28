package ro.uaic.feaa.sdbis.supplychain.comenzi.application;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ro.uaic.feaa.sdbis.supplychain.comenzi.api.IComenziModuleApi;
import ro.uaic.feaa.sdbis.supplychain.comenzi.api.command.CreeazaComandaPublicCommand;
import ro.uaic.feaa.sdbis.supplychain.comenzi.api.dto.ComandaSummaryDTO;
import ro.uaic.feaa.sdbis.supplychain.comenzi.api.dto.LinieComandaDTO;
import ro.uaic.feaa.sdbis.supplychain.comenzi.application.command.AnuleazaComandaCommand;
import ro.uaic.feaa.sdbis.supplychain.comenzi.application.command.ConfirmaComandaCommand;
import ro.uaic.feaa.sdbis.supplychain.comenzi.application.command.CreeazaComandaCommand;
import ro.uaic.feaa.sdbis.supplychain.comenzi.domain.*;
import ro.uaic.feaa.sdbis.supplychain.shared.CodMaterial;
import ro.uaic.feaa.sdbis.supplychain.shared.IdComanda;
import ro.uaic.feaa.sdbis.supplychain.shared.IdFurnizor;

import java.math.BigDecimal;
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
    private final ro.uaic.feaa.sdbis.supplychain.shared.OutboxRepo outboxRepo;

    public ComenziModuleFacade(ComandaApplicationService comandaAppService,
                               ComandaRepo comandaRepo,
                               ro.uaic.feaa.sdbis.supplychain.shared.OutboxRepo outboxRepo) {
        this.comandaAppService = comandaAppService;
        this.comandaRepo = comandaRepo;
        this.outboxRepo = outboxRepo;
    }

    @Override
    public int proceseazaTimeoutConfirmareComenzi() {
        LocalDate acum2Zile = LocalDate.now().minusDays(2);
        List<ComandaAprovizionare> comenzi = comandaRepo.findAll();
        int count = 0;
        for (ComandaAprovizionare c : comenzi) {
            if (c.getStareComanda() == StareComanda.Solicitat && c.getDataComanda().isBefore(acum2Zile)) {
                outboxRepo.saveEvent(new ro.uaic.feaa.sdbis.supplychain.comenzi.domain.event.ComandaNeconfirmataEvent(
                    UUID.randomUUID(), java.time.Instant.now(), c.getId()
                ));
                count++;
            }
        }
        return count;
    }

    @Override
    public UUID creeazaComanda(CreeazaComandaPublicCommand cmd) {
        List<LinieComanda> linii = cmd.linii().stream()
                .map(l -> new LinieComanda(
                        CodMaterial.of(l.material()),
                        Cantitate.of(l.cantitate().doubleValue(), l.unitateMasura()),
                        PretUnitar.of(l.pretUnitar().doubleValue(), l.moneda())
                ))
                .toList();

        String numar = "CMD-" + System.currentTimeMillis();
        CreeazaComandaCommand internalCmd = new CreeazaComandaCommand(
                numar,
                IdFurnizor.of(cmd.furnizorId()),
                cmd.gestionarId(),
                LocalDate.now(),
                cmd.dataLivrareEstimata(),
                cmd.depozitDestinatie(),
                linii
        );

        return comandaAppService.creeazaComanda(internalCmd).getValue();
    }

    @Override
    public void confirmaComanda(UUID comandaId, LocalDate dataLivrareConfirmata) {
        comandaAppService.confirmaComanda(new ConfirmaComandaCommand(IdComanda.of(comandaId), dataLivrareConfirmata));
    }

    @Override
    public void anuleazaComanda(UUID comandaId, String motiv) {
        comandaAppService.anuleazaComanda(new AnuleazaComandaCommand(IdComanda.of(comandaId)));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ComandaSummaryDTO> obtineDetaliiComanda(UUID comandaId) {
        return comandaRepo.findById(IdComanda.of(comandaId)).map(this::mapToDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean esteComandaEligibilaPentruReconciliere(UUID comandaId) {
        return comandaRepo.findById(IdComanda.of(comandaId))
                .map(c -> c.getStareComanda().name().contains("Receptionat"))
                .orElse(false);
    }

    private ComandaSummaryDTO mapToDTO(ComandaAprovizionare c) {
        List<LinieComandaDTO> linii = c.getLinii().stream()
                .map(l -> new LinieComandaDTO(
                        l.getMaterial().getCod(),
                        l.getCantitate().getValoare(),
                        l.getCantitate().getUnitateMasura(),
                        l.getPretUnitar().getValoare(),
                        l.getPretUnitar().getMoneda().getCurrencyCode()
                ))
                .toList();

        return new ComandaSummaryDTO(
                c.getId().getValue(),
                c.getNumarComanda(),
                c.getFurnizorId().getValue(),
                c.getStareComanda().name(),
                c.getDataLivrareCeruta(),
                linii
        );
    }
}
