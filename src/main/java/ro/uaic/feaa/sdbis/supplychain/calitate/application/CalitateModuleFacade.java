package ro.uaic.feaa.sdbis.supplychain.calitate.application;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ro.uaic.feaa.sdbis.supplychain.calitate.api.ICalitateModuleApi;
import ro.uaic.feaa.sdbis.supplychain.calitate.api.dto.FurnizorSummaryDTO;
import ro.uaic.feaa.sdbis.supplychain.calitate.api.event.CertificareInspectorExpiraPublicEvent;
import ro.uaic.feaa.sdbis.supplychain.calitate.api.event.FurnizorBlocatPublicEvent;
import ro.uaic.feaa.sdbis.supplychain.calitate.application.command.CalculScorecardCommand;
import ro.uaic.feaa.sdbis.supplychain.calitate.domain.Furnizor;
import ro.uaic.feaa.sdbis.supplychain.calitate.domain.FurnizorRepo;
import ro.uaic.feaa.sdbis.supplychain.calitate.domain.InspectieRepo;
import ro.uaic.feaa.sdbis.supplychain.calitate.domain.InspectorCalitate;
import ro.uaic.feaa.sdbis.supplychain.calitate.domain.InspectorRepo;
import ro.uaic.feaa.sdbis.supplychain.calitate.domain.PlanAQL;
import ro.uaic.feaa.sdbis.supplychain.calitate.domain.StareFurnizor;
import ro.uaic.feaa.sdbis.supplychain.calitate.domain.event.CertificareInspectorExpiraEvent;
import ro.uaic.feaa.sdbis.supplychain.calitate.domain.event.FurnizorBlocatEvent;
import ro.uaic.feaa.sdbis.supplychain.shared.CodMaterial;
import ro.uaic.feaa.sdbis.supplychain.shared.IFurnizorComandaAdapter;
import ro.uaic.feaa.sdbis.supplychain.shared.IdComanda;
import ro.uaic.feaa.sdbis.supplychain.shared.IdFurnizor;
import ro.uaic.feaa.sdbis.supplychain.shared.IdLot;
import ro.uaic.feaa.sdbis.supplychain.shared.OutboxRepo;
import ro.uaic.feaa.sdbis.supplychain.shared.Perioada;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Enterprise Facade implementing the public ICalitateModuleApi contract.
 */
@Component
public class CalitateModuleFacade implements ICalitateModuleApi {

    private static final Logger log = LoggerFactory.getLogger(CalitateModuleFacade.class);

    private final FurnizorRepo furnizorRepo;
    private final InspectorRepo inspectorRepo;
    private final InspectieRepo inspectieRepo;
    private final CalitateApplicationService calitateAppService;
    private final OutboxRepo outboxRepo;
    private final Optional<IFurnizorComandaAdapter> furnizorComandaAdapter;

    public CalitateModuleFacade(FurnizorRepo furnizorRepo,
                                InspectorRepo inspectorRepo,
                                InspectieRepo inspectieRepo,
                                CalitateApplicationService calitateAppService,
                                OutboxRepo outboxRepo,
                                Optional<IFurnizorComandaAdapter> furnizorComandaAdapter) {
        this.furnizorRepo = furnizorRepo;
        this.inspectorRepo = inspectorRepo;
        this.inspectieRepo = inspectieRepo;
        this.calitateAppService = calitateAppService;
        this.outboxRepo = outboxRepo;
        this.furnizorComandaAdapter = furnizorComandaAdapter;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean esteFurnizorEligibilPentruComenzi(UUID furnizorId) {
        return furnizorRepo.findById(IdFurnizor.of(furnizorId))
                .map(Furnizor::esteEligibilPentruComenzi)
                .orElse(false);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean esteFurnizorBlocat(UUID furnizorId) {
        return furnizorRepo.findById(IdFurnizor.of(furnizorId))
                .map(f -> f.getStareFurnizor() == StareFurnizor.Blocat || f.getStareFurnizor() == StareFurnizor.Suspendat)
                .orElse(false);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<FurnizorSummaryDTO> obtineDetaliiFurnizor(UUID furnizorId) {
        return furnizorRepo.findById(IdFurnizor.of(furnizorId)).map(this::mapToDTO);
    }

    @Override
    @Transactional
    public void blocheazaFurnizor(UUID furnizorId, String motiv) {
        furnizorRepo.findById(IdFurnizor.of(furnizorId)).ifPresent(f -> {
            FurnizorBlocatEvent evt = f.blocheaza(motiv);
            furnizorRepo.save(f);
            outboxRepo.saveEvent(evt);
            outboxRepo.saveEvent(new FurnizorBlocatPublicEvent(
                UUID.randomUUID(), evt.occurredOn(), furnizorId, motiv
            ));
        });
    }

    @Override
    public int proceseazaCalculScorecardLunarAutomat() {
        LocalDate azi = LocalDate.now();
        LocalDate primaZi = azi.withDayOfMonth(1);
        Perioada luna = new Perioada(primaZi, azi);

        List<Furnizor> furnizori = furnizorRepo.findEligibiliPentruComenzi();
        int count = 0;
        for (Furnizor f : furnizori) {
            try {
                BigDecimal rataRespingere = inspectieRepo.calculeazaRataRespingere(f.getIdFurnizor(), primaZi, azi);
                calitateAppService.calculeazaScorecardLunar(new CalculScorecardCommand(
                    f.getIdFurnizor(), luna, rataRespingere
                ));
                count++;
            } catch (Exception ex) {
                log.error("[Scorecard Lunar Automat] Eroare la calcularea scorecard-ului pentru furnizorul {} pe perioada {}: {}",
                        f.getIdFurnizor().getValue(), luna, ex.getMessage(), ex);
            }
        }
        return count;
    }

    @Override
    @Transactional
    public int proceseazaExpirareCertificariInspectori() {
        LocalDate in15Zile = LocalDate.now().plusDays(15);
        List<InspectorCalitate> inspectori = inspectorRepo.findAllWithDetails();
        int count = 0;
        for (InspectorCalitate insp : inspectori) {
            insp.getCertificari().forEach(cert -> {
                if (cert.getDataExpirare().isBefore(in15Zile)) {
                    outboxRepo.saveEvent(new CertificareInspectorExpiraEvent(
                        UUID.randomUUID(), Instant.now(), insp.getIdInspector(), cert.getDenumire(), cert.getDataExpirare()
                    ));
                    outboxRepo.saveEvent(new CertificareInspectorExpiraPublicEvent(
                        UUID.randomUUID(), Instant.now(), insp.getIdInspector(), cert.getDenumire(), cert.getDataExpirare()
                    ));
                }
            });
            count++;
        }
        return count;
    }

    @Override
    public UUID programeazaInspectie(UUID lotId, UUID comandaId, UUID furnizorId, String codMaterial) {
        LocalDate azi = LocalDate.now();
        Perioada perioadaInspectie = new Perioada(azi, azi.plusDays(3));
        PlanAQL plan = PlanAQL.standard(2.5, 30, 2);
        IdFurnizor furnizor = furnizorId != null ? IdFurnizor.of(furnizorId) : resolveFurnizorForComanda(comandaId);
        return calitateAppService.programeazaInspectie(
            IdLot.of(lotId),
            IdComanda.of(comandaId),
            furnizor,
            CodMaterial.of(codMaterial),
            plan,
            perioadaInspectie
        );
    }

    @Override
    public UUID programeazaInspectie(UUID lotId, UUID comandaId, String codMaterial) {
        return programeazaInspectie(lotId, comandaId, null, codMaterial);
    }

    private IdFurnizor resolveFurnizorForComanda(UUID comandaId) {
        if (comandaId != null && furnizorComandaAdapter != null && furnizorComandaAdapter.isPresent()) {
            Optional<IdFurnizor> resolved = furnizorComandaAdapter.get().gasesteFurnizorComanda(IdComanda.of(comandaId));
            if (resolved.isPresent()) {
                return resolved.get();
            }
        }
        return resolveFurnizorFallback();
    }

    private IdFurnizor resolveFurnizorFallback() {
        return furnizorRepo.findEligibiliPentruComenzi().stream()
                .findFirst()
                .map(Furnizor::getId)
                .orElseThrow(() -> new IllegalStateException(
                        "Nu exista niciun furnizor eligibil in sistem pentru asocierea inspectiei de calitate"));
    }

    private FurnizorSummaryDTO mapToDTO(Furnizor f) {
        return new FurnizorSummaryDTO(
                f.getId().getValue(),
                f.getCodFiscal(),
                f.getDenumire(),
                f.getStareFurnizor().name(),
                f.getIban().getValoare(),
                f.getCategoriiMateriale(),
                f.esteEligibilPentruComenzi(),
                f.getStareFurnizor() == StareFurnizor.Blocat || f.getStareFurnizor() == StareFurnizor.Suspendat
        );
    }
}
