package ro.uaic.feaa.sdbis.supplychain.facturi.application;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ro.uaic.feaa.sdbis.supplychain.facturi.api.IFacturiModuleApi;
import ro.uaic.feaa.sdbis.supplychain.facturi.api.dto.FacturaSummaryDTO;
import ro.uaic.feaa.sdbis.supplychain.facturi.application.command.CreeazaOrdinPlataCommand;
import ro.uaic.feaa.sdbis.supplychain.facturi.application.command.ReconciliereCommand;
import ro.uaic.feaa.sdbis.supplychain.facturi.domain.FacturaFurnizor;
import ro.uaic.feaa.sdbis.supplychain.facturi.domain.FacturaRepo;
import ro.uaic.feaa.sdbis.supplychain.facturi.domain.StareFactura;
import ro.uaic.feaa.sdbis.supplychain.facturi.domain.Suma;
import ro.uaic.feaa.sdbis.supplychain.facturi.domain.event.ScadentaApropiataEvent;
import ro.uaic.feaa.sdbis.supplychain.shared.IdComanda;
import ro.uaic.feaa.sdbis.supplychain.shared.OutboxRepo;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Enterprise Facade implementing the public IFacturiModuleApi contract.
 */
@Component
@Transactional
public class FacturiModuleFacade implements IFacturiModuleApi {

    private final FacturaApplicationService facturaAppService;
    private final FacturaRepo facturaRepo;
    private final OutboxRepo outboxRepo;

    public FacturiModuleFacade(FacturaApplicationService facturaAppService,
            FacturaRepo facturaRepo,
            OutboxRepo outboxRepo) {
        this.facturaAppService = facturaAppService;
        this.facturaRepo = facturaRepo;
        this.outboxRepo = outboxRepo;
    }

    @Override
    public void reconciliazaFactura(UUID facturaId) {
        facturaAppService.reconcilieazaFactura(new ReconciliereCommand(facturaId));
    }

    @Override
    public UUID initiazaPlata(UUID facturaId, BigDecimal suma, String moneda, UUID creatDe) {
        return facturaAppService.creeazaOrdinPlata(
                new CreeazaOrdinPlataCommand(facturaId, Suma.of(suma.doubleValue(), moneda), creatDe));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<FacturaSummaryDTO> obtineDetaliiFactura(UUID facturaId) {
        return facturaRepo.findById(facturaId).map(this::mapToDTO);
    }

    @Override
    public void anuleazaFacturiPentruComanda(UUID comandaId) {
        facturaAppService.blocheazaFacturiComandaAnulata(IdComanda.of(comandaId));
    }

    @Override
    public int proceseazaVerificareScadenteFacturi() {
        LocalDate in3Zile = LocalDate.now().plusDays(3);
        List<FacturaFurnizor> facturi = facturaRepo.findAll();
        int count = 0;
        for (FacturaFurnizor f : facturi) {
            if ((f.getStareFactura() == StareFactura.AprobataLaPlata
                    || f.getStareFactura() == StareFactura.PartialPlatita)
                    && f.getDataScadenta().isEqual(in3Zile)) {
                outboxRepo.saveEvent(new ScadentaApropiataEvent(
                        UUID.randomUUID(), Instant.now(), f.getIdFactura(), in3Zile));
                outboxRepo.saveEvent(new ro.uaic.feaa.sdbis.supplychain.facturi.api.event.ScadentaApropiataPublicEvent(
                        UUID.randomUUID(), Instant.now(), f.getIdFactura(), in3Zile));
                count++;
            }
        }
        return count;
    }

    private FacturaSummaryDTO mapToDTO(FacturaFurnizor f) {
        return new FacturaSummaryDTO(
                f.getIdFactura(),
                f.getSerie() + " " + f.getNumar(),
                f.getComandaId().getValue(),
                f.getFurnizorId().getValue(),
                f.getDataEmitere(),
                f.getDataScadenta(),
                f.getValoare().getValoareTotala(),
                f.getSumaPlatita().getValoare(),
                f.getSumaPlatita().getMoneda().getCurrencyCode(),
                f.getStareFactura().name());
    }
}