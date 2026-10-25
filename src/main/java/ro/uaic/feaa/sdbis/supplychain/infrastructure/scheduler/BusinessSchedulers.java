package ro.uaic.feaa.sdbis.supplychain.infrastructure.scheduler;

import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ro.uaic.feaa.sdbis.supplychain.calitate.application.CalitateApplicationService;
import ro.uaic.feaa.sdbis.supplychain.calitate.application.command.CalculScorecardCommand;
import ro.uaic.feaa.sdbis.supplychain.calitate.domain.Furnizor;
import ro.uaic.feaa.sdbis.supplychain.calitate.domain.FurnizorRepo;
import ro.uaic.feaa.sdbis.supplychain.calitate.domain.InspectorCalitate;
import ro.uaic.feaa.sdbis.supplychain.calitate.domain.InspectorRepo;
import ro.uaic.feaa.sdbis.supplychain.calitate.domain.event.CertificareInspectorExpiraEvent;
import ro.uaic.feaa.sdbis.supplychain.comenzi.domain.ComandaAprovizionare;
import ro.uaic.feaa.sdbis.supplychain.comenzi.domain.ComandaRepo;
import ro.uaic.feaa.sdbis.supplychain.comenzi.domain.StareComanda;
import ro.uaic.feaa.sdbis.supplychain.comenzi.domain.event.ComandaNeconfirmataEvent;
import ro.uaic.feaa.sdbis.supplychain.facturi.domain.FacturaFurnizor;
import ro.uaic.feaa.sdbis.supplychain.facturi.domain.FacturaRepo;
import ro.uaic.feaa.sdbis.supplychain.facturi.domain.StareFactura;
import ro.uaic.feaa.sdbis.supplychain.facturi.domain.event.ScadentaApropiataEvent;
import ro.uaic.feaa.sdbis.supplychain.notificari.NotificariDispatcherService;
import ro.uaic.feaa.sdbis.supplychain.shared.OutboxRepo;
import ro.uaic.feaa.sdbis.supplychain.shared.Perioada;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Cele 4 Schedulere de Business conforme cu Tabelul 1.1 si Secțiunea 8 din Master Blueprint:
 * 1. ComandaConfirmareTimeout (48h)
 * 2. CalculCalificativLunar (Ultima zi a lunii)
 * 3. ExpirareCertificariInspectori (Zilnic la 08:00)
 * 4. VerificareScadenteFacturi (Zilnic la 08:30)
 */
@Component
public class BusinessSchedulers {

    private final ComandaRepo comandaRepo;
    private final FurnizorRepo furnizorRepo;
    private final InspectorRepo inspectorRepo;
    private final FacturaRepo facturaRepo;
    private final OutboxRepo outboxRepo;
    private final NotificariDispatcherService notificariService;
    private final CalitateApplicationService calitateAppService;

    public BusinessSchedulers(ComandaRepo comandaRepo,
                              FurnizorRepo furnizorRepo,
                              InspectorRepo inspectorRepo,
                              FacturaRepo facturaRepo,
                              OutboxRepo outboxRepo,
                              NotificariDispatcherService notificariService,
                              @Lazy CalitateApplicationService calitateAppService) {
        this.comandaRepo = comandaRepo;
        this.furnizorRepo = furnizorRepo;
        this.inspectorRepo = inspectorRepo;
        this.facturaRepo = facturaRepo;
        this.outboxRepo = outboxRepo;
        this.notificariService = notificariService;
        this.calitateAppService = calitateAppService;
    }

    @Scheduled(cron = "0 0 * * * *")
    public void verificaTimeoutConfirmareComenzi() {
        LocalDate acum2Zile = LocalDate.now().minusDays(2);
        List<ComandaAprovizionare> comenzi = comandaRepo.findAll();
        for (ComandaAprovizionare c : comenzi) {
            if (c.getStareComanda() == StareComanda.Solicitat && c.getDataComanda().isBefore(acum2Zile)) {
                outboxRepo.saveEvent(new ComandaNeconfirmataEvent(
                    UUID.randomUUID(), Instant.now(), c.getId()
                ));
            }
        }
    }

    @Scheduled(cron = "0 0 23 L * *")
    public void calculScorecardLunarAutomat() {
        LocalDate azi = LocalDate.now();
        LocalDate primaZi = azi.withDayOfMonth(1);
        Perioada luna = new Perioada(primaZi, azi);

        List<Furnizor> furnizori = furnizorRepo.findAll();
        for (Furnizor f : furnizori) {
            if (f.esteEligibilPentruComenzi()) {
                try {
                    calitateAppService.calculeazaScorecardLunar(new CalculScorecardCommand(
                        f.getIdFurnizor(), luna, BigDecimal.valueOf(2.0)
                    ));
                } catch (Exception ignored) {
                }
            }
        }
    }

    @Scheduled(cron = "0 0 8 * * *")
    public void verificaExpirareCertificari() {
        LocalDate in15Zile = LocalDate.now().plusDays(15);
        List<InspectorCalitate> inspectori = inspectorRepo.findAll();
        for (InspectorCalitate insp : inspectori) {
            insp.getCertificari().forEach(cert -> {
                if (cert.getDataExpirare().isBefore(in15Zile)) {
                    outboxRepo.saveEvent(new CertificareInspectorExpiraEvent(
                        UUID.randomUUID(), Instant.now(), insp.getIdInspector(), cert.getDenumire(), cert.getDataExpirare()
                    ));
                }
            });
        }
    }

    @Scheduled(cron = "0 30 8 * * *")
    public void verificaScadenteFacturi() {
        LocalDate in3Zile = LocalDate.now().plusDays(3);
        List<FacturaFurnizor> facturi = facturaRepo.findAll();
        for (FacturaFurnizor f : facturi) {
            if ((f.getStareFactura() == StareFactura.AprobataLaPlata || f.getStareFactura() == StareFactura.PartialPlatita)
                && f.getDataScadenta().isEqual(in3Zile)) {
                outboxRepo.saveEvent(new ScadentaApropiataEvent(
                    UUID.randomUUID(), Instant.now(), f.getIdFactura(), in3Zile
                ));
            }
        }
    }
}
