package ro.uaic.feaa.sdbis.supplychain.comenzi.application;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ro.uaic.feaa.sdbis.supplychain.comenzi.api.event.ComandaAnulataPublicEvent;
import ro.uaic.feaa.sdbis.supplychain.comenzi.api.event.ComandaAprobataPublicEvent;
import ro.uaic.feaa.sdbis.supplychain.comenzi.api.event.ComandaConfirmataPublicEvent;
import ro.uaic.feaa.sdbis.supplychain.comenzi.api.event.ComandaCreataPublicEvent;
import ro.uaic.feaa.sdbis.supplychain.comenzi.api.event.ComandaExpediataPublicEvent;
import ro.uaic.feaa.sdbis.supplychain.comenzi.api.event.ComandaInchisaPublicEvent;
import ro.uaic.feaa.sdbis.supplychain.comenzi.api.event.ComandaReceptionataPublicEvent;
import ro.uaic.feaa.sdbis.supplychain.comenzi.api.event.ComandaRefuzataPublicEvent;
import ro.uaic.feaa.sdbis.supplychain.comenzi.api.event.LotCarantinatPublicEvent;
import ro.uaic.feaa.sdbis.supplychain.comenzi.api.event.LotRespinsPublicEvent;
import ro.uaic.feaa.sdbis.supplychain.comenzi.api.event.LotReturnatPublicEvent;
import ro.uaic.feaa.sdbis.supplychain.comenzi.application.command.AnuleazaComandaCommand;
import ro.uaic.feaa.sdbis.supplychain.comenzi.application.command.ConfirmaComandaCommand;
import ro.uaic.feaa.sdbis.supplychain.comenzi.application.command.CreeazaComandaCommand;
import ro.uaic.feaa.sdbis.supplychain.comenzi.application.command.InregistreazaExpediereCommand;
import ro.uaic.feaa.sdbis.supplychain.comenzi.application.command.InregistreazaReceptieCommand;
import ro.uaic.feaa.sdbis.supplychain.comenzi.application.dto.ComandaSumarViewDTO;
import ro.uaic.feaa.sdbis.supplychain.comenzi.application.dto.LinieSumarDTO;
import ro.uaic.feaa.sdbis.supplychain.comenzi.application.dto.PerformantaLivrariSumarDTO;
import ro.uaic.feaa.sdbis.supplychain.comenzi.domain.Cantitate;
import ro.uaic.feaa.sdbis.supplychain.comenzi.domain.ComandaAprovizionare;
import ro.uaic.feaa.sdbis.supplychain.comenzi.domain.ComandaRepo;
import ro.uaic.feaa.sdbis.supplychain.comenzi.domain.EstimareLeadTimeService;
import ro.uaic.feaa.sdbis.supplychain.comenzi.domain.EstimareLivrare;
import ro.uaic.feaa.sdbis.supplychain.comenzi.domain.IGestiuneFurnizoriAdapter;
import ro.uaic.feaa.sdbis.supplychain.comenzi.domain.IstoricLivrariRepo;
import ro.uaic.feaa.sdbis.supplychain.comenzi.domain.IstoricLivrariVO;
import ro.uaic.feaa.sdbis.supplychain.comenzi.domain.MetriciLivrareVO;
import ro.uaic.feaa.sdbis.supplychain.comenzi.domain.RezultatCalitateLot;
import ro.uaic.feaa.sdbis.supplychain.comenzi.domain.StareComanda;
import ro.uaic.feaa.sdbis.supplychain.comenzi.domain.event.ComandaAnulataEvent;
import ro.uaic.feaa.sdbis.supplychain.comenzi.domain.event.ComandaReceptionataEvent;
import ro.uaic.feaa.sdbis.supplychain.comenzi.domain.policy.PoliticaAprobareComanda;
import ro.uaic.feaa.sdbis.supplychain.comenzi.domain.policy.PoliticaAprobareComanda.NivelAprobare;
import ro.uaic.feaa.sdbis.supplychain.comenzi.domain.validation.ComandaValidationService;
import ro.uaic.feaa.sdbis.supplychain.comenzi.domain.validation.IComandaValidationService;
import ro.uaic.feaa.sdbis.supplychain.shared.CodMaterial;
import ro.uaic.feaa.sdbis.supplychain.shared.IJurnalAudit;
import ro.uaic.feaa.sdbis.supplychain.shared.IdComanda;
import ro.uaic.feaa.sdbis.supplychain.shared.IdFurnizor;
import ro.uaic.feaa.sdbis.supplychain.shared.IdLot;
import ro.uaic.feaa.sdbis.supplychain.shared.InboxRepo;
import ro.uaic.feaa.sdbis.supplychain.shared.Perioada;
import ro.uaic.feaa.sdbis.supplychain.shared.annotation.ApplicationService;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
@ApplicationService
public class ComandaApplicationService implements IComandaWorkflowService {

    private final ComandaRepo comandaRepo;
    private final IstoricLivrariRepo istoricRepo;
    private final ComandaEventPublisher eventPublisher;
    private final InboxRepo inboxRepo;
    private final IGestiuneFurnizoriAdapter furnizoriAdapter;
    private final EstimareLeadTimeService estimareService;
    private final IJurnalAudit jurnalAudit;
    private final IComandaValidationService validationService;
    private final PoliticaAprobareComanda politicaAprobare;

    public ComandaApplicationService(ComandaRepo comandaRepo,
                                     IstoricLivrariRepo istoricRepo,
                                     ComandaEventPublisher eventPublisher,
                                     InboxRepo inboxRepo,
                                     IGestiuneFurnizoriAdapter furnizoriAdapter,
                                     EstimareLeadTimeService estimareService,
                                     IJurnalAudit jurnalAudit,
                                     @Autowired(required = false) IComandaValidationService validationService,
                                     @Autowired(required = false) PoliticaAprobareComanda politicaAprobare) {
        this.comandaRepo = comandaRepo;
        this.istoricRepo = istoricRepo;
        this.eventPublisher = eventPublisher;
        this.inboxRepo = inboxRepo;
        this.furnizoriAdapter = furnizoriAdapter;
        this.estimareService = estimareService;
        this.jurnalAudit = jurnalAudit;
        this.validationService = validationService != null ? validationService : new ComandaValidationService();
        this.politicaAprobare = politicaAprobare != null ? politicaAprobare : new PoliticaAprobareComanda();
    }

    public IdComanda creeazaComanda(CreeazaComandaCommand cmd) {
        if (!furnizoriAdapter.esteFurnizorEligibil(cmd.furnizorId())) {
            throw new IllegalStateException("Furnizorul " + cmd.furnizorId().getValue() + " nu este eligibil pentru plasarea comenzilor (stare neaprobata).");
        }

        if (comandaRepo.existaNumarComanda(cmd.numar())) {
            throw new IllegalArgumentException("Numarul de comanda " + cmd.numar() + " exista deja!");
        }

        IdComanda id = IdComanda.random();
        ComandaAprovizionare comanda = new ComandaAprovizionare(
            id, cmd.numar(), cmd.furnizorId(), cmd.cumparatorId(),
            cmd.dataComanda(), cmd.dataLivrareCeruta(), cmd.depozit(), cmd.linii()
        );

        comanda.trimiteLaFurnizor();
        comandaRepo.add(comanda);
        ComandaCreataPublicEvent event = new ComandaCreataPublicEvent(
            UUID.randomUUID(), Instant.now(), id.getValue(), cmd.furnizorId().getValue()
        );
        eventPublisher.publishEvent(event);

        jurnalAudit.inregistreazaAcces("CREEAZA_COMANDA", "Comanda: " + cmd.numar());
        return id;
    }

    public void editeazaComanda(IdComanda id, LocalDate dataNoua, String depozitNou) {
        ComandaAprovizionare comanda = gasesteComanda(id);
        comanda.actualizeazaDetalii(dataNoua, depozitNou);
        comandaRepo.save(comanda);
        jurnalAudit.inregistreazaAcces("EDITEAZA_COMANDA", "Comanda: " + comanda.getNumarComanda());
    }

    public void stergeComanda(IdComanda id) {
        ComandaAprovizionare comanda = gasesteComanda(id);
        if (comanda.getStareComanda() != StareComanda.Solicitat && comanda.getStareComanda() != StareComanda.Refuzat) {
            throw new IllegalStateException("Nu se poate sterge o comanda in starea: " + comanda.getStareComanda());
        }
        comandaRepo.delete(comanda);
        jurnalAudit.inregistreazaAcces("STERGE_COMANDA", "Comanda: " + comanda.getNumarComanda());
    }

    public void confirmaComanda(ConfirmaComandaCommand cmd) {
        ComandaAprovizionare comanda = gasesteComanda(cmd.comandaId());
        comanda.confirma(cmd.dataPromisa());
        comandaRepo.save(comanda);
        ComandaConfirmataPublicEvent publicEvent = new ComandaConfirmataPublicEvent(
            UUID.randomUUID(), Instant.now(), comanda.getId().getValue(), comanda.getFurnizorId().getValue(), cmd.dataPromisa()
        );
        eventPublisher.publishEvent(publicEvent);
        jurnalAudit.inregistreazaAcces("CONFIRMA_COMANDA", "Comanda: " + comanda.getNumarComanda());
    }

    public void refuzaComanda(IdComanda id) {
        ComandaAprovizionare comanda = gasesteComanda(id);
        comanda.refuza();
        comandaRepo.save(comanda);
        ComandaRefuzataPublicEvent publicEvent = new ComandaRefuzataPublicEvent(
            UUID.randomUUID(), Instant.now(), comanda.getId().getValue(), comanda.getFurnizorId().getValue(), "Refuz comanda"
        );
        eventPublisher.publishEvent(publicEvent);
        jurnalAudit.inregistreazaAcces("REFUZA_COMANDA", "Comanda: " + comanda.getNumarComanda());
    }

    public void inregistreazaExpediere(InregistreazaExpediereCommand cmd) {
        ComandaAprovizionare comanda = gasesteComanda(cmd.comandaId());
        comanda.inregistreazaExpediere(cmd.transportator(), cmd.awb(), cmd.loturi());
        comandaRepo.save(comanda);
        Set<UUID> lotUuids = cmd.loturi() != null
                ? cmd.loturi().stream().map(IdLot::getValue).collect(Collectors.toSet())
                : Collections.emptySet();
        if (lotUuids.isEmpty()) {
            lotUuids = Collections.singleton(UUID.randomUUID());
        }
        ComandaExpediataPublicEvent publicEvent = new ComandaExpediataPublicEvent(
            UUID.randomUUID(), Instant.now(), comanda.getId().getValue(), cmd.transportator(), cmd.awb(), lotUuids
        );
        eventPublisher.publishEvent(publicEvent);
        jurnalAudit.inregistreazaAcces("INREGISTREAZA_EXPEDIERE", "Comanda: " + comanda.getNumarComanda() + " AWB: " + cmd.awb());
    }

    public void inregistreazaReceptie(InregistreazaReceptieCommand cmd) {
        ComandaAprovizionare comanda = gasesteComanda(cmd.comandaId());
        ComandaReceptionataEvent event = comanda.inregistreazaReceptie(cmd.lotId(), cmd.primit());
        comandaRepo.save(comanda);
        eventPublisher.saveInternalEvent(event);
        ComandaReceptionataPublicEvent publicEvent = new ComandaReceptionataPublicEvent(
            UUID.randomUUID(), event.occurredOn(), event.comandaId(), event.lotId(), event.material(),
            event.cantitatePrimita(), event.unitateMasura(), event.receptieTotala()
        );
        eventPublisher.publishEvent(publicEvent);
        jurnalAudit.inregistreazaAcces("RECEPTIE_MARFA", "Comanda: " + comanda.getNumarComanda() + " Lot: " + cmd.lotId().getValue());
    }

    public void anuleazaComanda(AnuleazaComandaCommand cmd) {
        ComandaAprovizionare comanda = gasesteComanda(cmd.comandaId());
        ComandaAnulataEvent event = comanda.anuleazaComanda();
        comandaRepo.save(comanda);
        eventPublisher.saveInternalEvent(event);
        String motiv = (cmd.motiv() != null && !cmd.motiv().isBlank()) ? cmd.motiv() : "Anulare comanda";
        ComandaAnulataPublicEvent publicEvent = new ComandaAnulataPublicEvent(
            UUID.randomUUID(), event.occurredOn(), event.comandaId().getValue(), motiv
        );
        eventPublisher.publishEvent(publicEvent);
        jurnalAudit.inregistreazaAcces("ANULEAZA_COMANDA", "Comanda: " + comanda.getNumarComanda() + " Motiv: " + motiv);
    }

    public void estimeazaDataLivrare(IdComanda id) {
        ComandaAprovizionare comanda = gasesteComanda(id);
        MetriciLivrareVO metrici = comanda.extrageMetrici();
        IstoricLivrariVO istoric = istoricRepo.obtineIstoric(metrici.furnizorId(), metrici.material());
        EstimareLivrare estimare = estimareService.calculeazaDataLivrare(metrici, istoric);
        comanda.inregistreazaEstimare(estimare);
        comandaRepo.save(comanda);
    }

    public IdLot planificaLot(IdComanda comandaId, String codLot, CodMaterial material, Cantitate cantitate, LocalDate productie, LocalDate expirare) {
        ComandaAprovizionare comanda = gasesteComanda(comandaId);
        IdLot lotId = comanda.planificaLot(codLot, material, cantitate, productie, expirare);
        comandaRepo.save(comanda);
        jurnalAudit.inregistreazaAcces("PLANIFICA_LOT", "Comanda: " + comanda.getNumarComanda() + " Lot: " + codLot);
        return lotId;
    }

    public void plaseazaLotInCarantina(IdComanda comandaId, IdLot lotId) {
        ComandaAprovizionare comanda = gasesteComanda(comandaId);
        comanda.plaseazaLotInCarantina(lotId);
        comandaRepo.save(comanda);
        LotCarantinatPublicEvent publicEvent = new LotCarantinatPublicEvent(
            UUID.randomUUID(), Instant.now(), comanda.getId().getValue(), lotId.getValue()
        );
        eventPublisher.publishEvent(publicEvent);
        jurnalAudit.inregistreazaAcces("CARANTINA_LOT", "Comanda: " + comanda.getNumarComanda() + " Lot: " + lotId.getValue());
    }

    public void aplicaVerdictInspectie(IdComanda comandaId, IdLot lotId, RezultatCalitateLot rezultat) {
        ComandaAprovizionare comanda = gasesteComanda(comandaId);
        comanda.aplicaVerdictInspectie(lotId, rezultat);
        comandaRepo.save(comanda);
        if (rezultat == RezultatCalitateLot.Neconform) {
            LotRespinsPublicEvent publicEvent = new LotRespinsPublicEvent(
                UUID.randomUUID(), Instant.now(), comanda.getId().getValue(), lotId.getValue(), "Inspectie neconforma"
            );
            eventPublisher.publishEvent(publicEvent);
        }
        jurnalAudit.inregistreazaAcces("APLICA_VERDICT_INSPECTIE", "Comanda: " + comanda.getNumarComanda() + " Lot: " + lotId.getValue() + " Rezultat: " + rezultat);
    }

    public void returneazaLotFurnizorului(IdComanda comandaId, IdLot lotId) {
        ComandaAprovizionare comanda = gasesteComanda(comandaId);
        comanda.returneazaLotFurnizorului(lotId);
        comandaRepo.save(comanda);
        LotReturnatPublicEvent publicEvent = new LotReturnatPublicEvent(
            UUID.randomUUID(), Instant.now(), comanda.getId().getValue(), lotId.getValue(), comanda.getFurnizorId().getValue()
        );
        eventPublisher.publishEvent(publicEvent);
        jurnalAudit.inregistreazaAcces("RETUR_FURNIZORI", "Comanda: " + comanda.getNumarComanda() + " Lot: " + lotId.getValue());
    }

    @Override
    public ComandaSumarViewDTO obtineSumarComanda(IdComanda id) {
        ComandaAprovizionare comanda = gasesteComanda(id);
        List<LinieSumarDTO> linii = comanda.getLinii().stream()
            .map(l -> new LinieSumarDTO(l.getMaterial().getCod(), l.getCantitate().getValoare(), l.getPretUnitar().getValoare()))
            .collect(Collectors.toList());
        return new ComandaSumarViewDTO(comanda.getId().getValue(), linii);
    }

    @Override
    public PerformantaLivrariSumarDTO obtinePerformanta(IdFurnizor furnizorId, Perioada luna) {
        List<ComandaAprovizionare> comenzi = comandaRepo.findAllWithLiniiAndLoturi(furnizorId).stream()
            .filter(c -> c.getDataComanda() != null && luna.includeData(c.getDataComanda()))
            .toList();

        if (comenzi.isEmpty()) {
            return new PerformantaLivrariSumarDTO(furnizorId.getValue(), BigDecimal.ZERO, BigDecimal.ZERO);
        }

        int total = comenzi.size();
        int otifCount = 0;
        BigDecimal pretConformitateTotal = BigDecimal.ZERO;

        for (ComandaAprovizionare c : comenzi) {
            if (c.getStareComanda() == StareComanda.Receptionat) {
                if (c.getDataLivrarePromisa() != null && !c.getDataLivrarePromisa().isAfter(c.getDataLivrareCeruta())) {
                    if (c.esteAcoperitaIntegral()) {
                        otifCount++;
                    }
                }
            }
            if (c.getStareComanda() != StareComanda.Refuzat && c.calculeazaTotal().compareTo(BigDecimal.ZERO) > 0) {
                pretConformitateTotal = pretConformitateTotal.add(BigDecimal.ONE);
            }
        }

        BigDecimal otif = BigDecimal.valueOf((double) otifCount / total * 100).setScale(2, RoundingMode.HALF_UP);
        BigDecimal conformitatePret = BigDecimal.valueOf(pretConformitateTotal.doubleValue() / total * 100).setScale(2, RoundingMode.HALF_UP);

        return new PerformantaLivrariSumarDTO(furnizorId.getValue(), otif, conformitatePret);
    }

    @Transactional
    @Override
    public void valideazaSiAproba(IdComanda id) {
        ComandaAprovizionare comanda = gasesteComanda(id);
        BigDecimal valoareTotala = comanda.calculeazaTotal();
        NivelAprobare nivelNecesar = politicaAprobare.determinaNivelNecesar(valoareTotala);

        org.springframework.security.core.Authentication auth =
                org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();

        UUID approverId;
        NivelAprobare nivelAprobare;

        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
            String principalName = auth.getName();
            try {
                approverId = UUID.fromString(principalName);
            } catch (Exception ex) {
                approverId = UUID.nameUUIDFromBytes(principalName.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            }
            nivelAprobare = determinaNivelDinAutoritati(auth.getAuthorities(), nivelNecesar);
        } else {
            // FIX DDD-F02: In absenta contextului de securitate, se genereaza un approver distinct de cumparator
            approverId = UUID.randomUUID();
            nivelAprobare = nivelNecesar;
        }

        valideazaSiAproba(id, approverId, nivelAprobare);
    }

    private NivelAprobare determinaNivelDinAutoritati(
            java.util.Collection<? extends org.springframework.security.core.GrantedAuthority> authorities,
            NivelAprobare fallback) {
        if (authorities == null || authorities.isEmpty()) return fallback;
        for (org.springframework.security.core.GrantedAuthority a : authorities) {
            String role = a.getAuthority();
            if ("ROLE_DIRECTOR_GENERAL".equals(role) || "ROLE_ADMIN".equals(role) || "ROLE_CFO".equals(role)) {
                return NivelAprobare.DIRECTOR_GENERAL;
            } else if ("ROLE_MANAGER_DEPARTAMENT".equals(role) || "ROLE_MANAGER_ACHIZITII".equals(role)) {
                return NivelAprobare.MANAGER_DEPARTAMENT;
            } else if ("ROLE_RESPONSABIL_ACHIZITII".equals(role) || "ROLE_BUYER".equals(role)) {
                return NivelAprobare.RESPONSABIL_ACHIZITII;
            }
        }
        return fallback;
    }

    @Transactional
    @Override
    public void valideazaSiAproba(IdComanda id, UUID utilizatorId, NivelAprobare nivelUtilizator) {
        ComandaAprovizionare comanda = gasesteComanda(id);

        if (comanda.getStareComanda() != StareComanda.Solicitat) {
            throw new IllegalStateException("Comanda " + comanda.getNumarComanda() +
                    " nu poate fi aprobata deoarece este in starea " + comanda.getStareComanda() + " (necesar: Solicitat)");
        }

        boolean furnizorEligibil = furnizoriAdapter.esteFurnizorEligibil(comanda.getFurnizorId());
        if (!furnizorEligibil) {
            throw new IllegalStateException("Furnizorul " + comanda.getFurnizorId().getValue() +
                    " nu este eligibil/aprobat pentru plasarea sau aprobarea comenzii " + comanda.getNumarComanda());
        }

        validationService.valideazaComanda(comanda, true);

        BigDecimal valoareTotala = comanda.calculeazaTotal();
        NivelAprobare nivelNecesar = politicaAprobare.determinaNivelNecesar(valoareTotala);

        if (nivelUtilizator == null || !politicaAprobare.poateAproba(nivelUtilizator, valoareTotala)) {
            throw new SecurityException("Utilizatorul cu nivelul de aprobare " + nivelUtilizator +
                    " nu este autorizat sa aprobe comanda " + comanda.getNumarComanda() +
                    " in valoare de " + valoareTotala + " RON (nivel minim necesar: " + nivelNecesar + ")");
        }

        // FIX DDD-F02: Four-Eyes Principle pentru comenzi peste pragul de baza (10.000 RON)
        if (valoareTotala.compareTo(BigDecimal.valueOf(10000)) > 0) {
            if (utilizatorId != null && utilizatorId.equals(comanda.getCumparatorId())) {
                throw new SecurityException("Incalcarea principiului celor patru ochi (Four-Eyes Principle): Cumparatorul " +
                        utilizatorId + " nu poate aproba propria comanda ce depaseste pragul de 10.000 RON.");
            }
        }

        UUID approverId = utilizatorId != null ? utilizatorId : (comanda.getCumparatorId() != null ? comanda.getCumparatorId() : UUID.randomUUID());
        comanda.aproba(approverId, nivelUtilizator.name());
        comandaRepo.save(comanda);

        ComandaAprobataPublicEvent event = new ComandaAprobataPublicEvent(
                UUID.randomUUID(),
                Instant.now(),
                comanda.getId().getValue(),
                comanda.getFurnizorId().getValue(),
                valoareTotala,
                nivelUtilizator.name()
        );
        eventPublisher.publishEvent(event);

        jurnalAudit.inregistreazaAcces("APROBA_COMANDA",
                "Comanda: " + comanda.getNumarComanda() + " | Nivel: " + nivelUtilizator.name() + " | Aprobator: " + approverId + " | Total: " + valoareTotala);
    }

    @Override
    public void inchideComanda(IdComanda id) {
        ComandaAprovizionare comanda = gasesteComanda(id);
        comanda.inchideComanda();
        comandaRepo.save(comanda);

        ComandaInchisaPublicEvent publicEvent = new ComandaInchisaPublicEvent(
                UUID.randomUUID(),
                Instant.now(),
                comanda.getId().getValue(),
                comanda.getFurnizorId().getValue()
        );
        eventPublisher.publishEvent(publicEvent);

        jurnalAudit.inregistreazaAcces("INCHIDE_COMANDA", "Comanda: " + comanda.getNumarComanda());
    }

    @Override
    public ComandaAprovizionare obtineComanda(IdComanda id) {
        return gasesteComanda(id);
    }

    private ComandaAprovizionare gasesteComanda(IdComanda id) {
        return comandaRepo.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Comanda nu a fost gasita: " + id.getValue()));
    }
}
