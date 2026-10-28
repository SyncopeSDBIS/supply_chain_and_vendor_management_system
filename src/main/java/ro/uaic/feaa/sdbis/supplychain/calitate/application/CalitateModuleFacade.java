package ro.uaic.feaa.sdbis.supplychain.calitate.application;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ro.uaic.feaa.sdbis.supplychain.calitate.api.ICalitateModuleApi;
import ro.uaic.feaa.sdbis.supplychain.calitate.api.dto.FurnizorSummaryDTO;
import ro.uaic.feaa.sdbis.supplychain.calitate.application.command.CalculScorecardCommand;
import ro.uaic.feaa.sdbis.supplychain.calitate.domain.Furnizor;
import ro.uaic.feaa.sdbis.supplychain.calitate.domain.FurnizorRepo;
import ro.uaic.feaa.sdbis.supplychain.calitate.domain.InspectorCalitate;
import ro.uaic.feaa.sdbis.supplychain.calitate.domain.InspectorRepo;
import ro.uaic.feaa.sdbis.supplychain.calitate.domain.StareFurnizor;
import ro.uaic.feaa.sdbis.supplychain.calitate.domain.event.CertificareInspectorExpiraEvent;
import ro.uaic.feaa.sdbis.supplychain.shared.IdFurnizor;
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
@Transactional
public class CalitateModuleFacade implements ICalitateModuleApi {

    private final FurnizorRepo furnizorRepo;
    private final InspectorRepo inspectorRepo;
    private final CalitateApplicationService calitateAppService;
    private final OutboxRepo outboxRepo;

    public CalitateModuleFacade(FurnizorRepo furnizorRepo,
                                InspectorRepo inspectorRepo,
                                @Lazy CalitateApplicationService calitateAppService,
                                OutboxRepo outboxRepo) {
        this.furnizorRepo = furnizorRepo;
        this.inspectorRepo = inspectorRepo;
        this.calitateAppService = calitateAppService;
        this.outboxRepo = outboxRepo;
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
    public void blocheazaFurnizor(UUID furnizorId, String motiv) {
        furnizorRepo.findById(IdFurnizor.of(furnizorId)).ifPresent(f -> {
            ro.uaic.feaa.sdbis.supplychain.calitate.domain.event.FurnizorBlocatEvent evt = f.blocheaza(motiv);
            furnizorRepo.save(f);
            outboxRepo.saveEvent(evt);
            outboxRepo.saveEvent(new ro.uaic.feaa.sdbis.supplychain.calitate.api.event.FurnizorBlocatPublicEvent(
                UUID.randomUUID(), evt.occurredOn(), furnizorId, motiv
            ));
        });
    }

    @Override
    public int proceseazaCalculScorecardLunarAutomat() {
        LocalDate azi = LocalDate.now();
        LocalDate primaZi = azi.withDayOfMonth(1);
        Perioada luna = new Perioada(primaZi, azi);

        List<Furnizor> furnizori = furnizorRepo.findAll();
        int count = 0;
        for (Furnizor f : furnizori) {
            if (f.esteEligibilPentruComenzi()) {
                try {
                    calitateAppService.calculeazaScorecardLunar(new CalculScorecardCommand(
                        f.getIdFurnizor(), luna, BigDecimal.valueOf(2.0)
                    ));
                    count++;
                } catch (Exception ignored) {
                }
            }
        }
        return count;
    }

    @Override
    public int proceseazaExpirareCertificariInspectori() {
        LocalDate in15Zile = LocalDate.now().plusDays(15);
        List<InspectorCalitate> inspectori = inspectorRepo.findAll();
        int count = 0;
        for (InspectorCalitate insp : inspectori) {
            insp.getCertificari().forEach(cert -> {
                if (cert.getDataExpirare().isBefore(in15Zile)) {
                    outboxRepo.saveEvent(new CertificareInspectorExpiraEvent(
                        UUID.randomUUID(), Instant.now(), insp.getIdInspector(), cert.getDenumire(), cert.getDataExpirare()
                    ));
                    outboxRepo.saveEvent(new ro.uaic.feaa.sdbis.supplychain.calitate.api.event.CertificareInspectorExpiraPublicEvent(
                        UUID.randomUUID(), Instant.now(), insp.getIdInspector(), cert.getDenumire(), cert.getDataExpirare()
                    ));
                }
            });
            count++;
        }
        return count;
    }

    @Override
    public UUID programeazaInspectie(UUID lotId, UUID comandaId, String codMaterial) {
        LocalDate azi = LocalDate.now();
        Perioada perioadaInspectie = new Perioada(azi, azi.plusDays(3));
        ro.uaic.feaa.sdbis.supplychain.calitate.domain.PlanAQL plan = ro.uaic.feaa.sdbis.supplychain.calitate.domain.PlanAQL.standard(2.5, 30, 2);
        return calitateAppService.programeazaInspectie(
            ro.uaic.feaa.sdbis.supplychain.shared.IdLot.of(lotId),
            ro.uaic.feaa.sdbis.supplychain.shared.IdComanda.of(comandaId),
            IdFurnizor.random(),
            ro.uaic.feaa.sdbis.supplychain.shared.CodMaterial.of(codMaterial),
            plan,
            perioadaInspectie
        );
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
