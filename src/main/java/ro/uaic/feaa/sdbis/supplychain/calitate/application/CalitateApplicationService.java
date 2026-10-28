package ro.uaic.feaa.sdbis.supplychain.calitate.application;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ro.uaic.feaa.sdbis.supplychain.calitate.application.command.*;
import ro.uaic.feaa.sdbis.supplychain.calitate.domain.*;
import ro.uaic.feaa.sdbis.supplychain.calitate.domain.event.FurnizorBlocatEvent;
import ro.uaic.feaa.sdbis.supplychain.calitate.domain.event.FurnizorSuspendatEvent;
import ro.uaic.feaa.sdbis.supplychain.calitate.domain.event.InspectieProgramataEvent;
import ro.uaic.feaa.sdbis.supplychain.calitate.domain.event.NCREscaladatEvent;
import ro.uaic.feaa.sdbis.supplychain.calitate.domain.event.VerdictInspectieEvent;
import ro.uaic.feaa.sdbis.supplychain.shared.*;
import ro.uaic.feaa.sdbis.supplychain.shared.annotation.ApplicationService;

import java.time.Instant;
import java.time.LocalDate;
import java.util.*;

@Service
@Transactional
@ApplicationService
public class CalitateApplicationService implements ICalitateWorkflowService {

    private final FurnizorRepo furnizorRepo;
    private final InspectorRepo inspectorRepo;
    private final InspectieRepo inspectieRepo;
    private final NCRRepo ncrRepo;
    private final OutboxRepo outboxRepo;
    private final InboxRepo inboxRepo;
    private final IPerformantaLivrariAdapter performantaAdapter;
    private final IJurnalAudit jurnalAudit;
    private final AlocareInspectorService alocareService;
    private final CalculScorecardService scorecardService;

    public CalitateApplicationService(FurnizorRepo furnizorRepo,
                                     InspectorRepo inspectorRepo,
                                     InspectieRepo inspectieRepo,
                                     NCRRepo ncrRepo,
                                     OutboxRepo outboxRepo,
                                     InboxRepo inboxRepo,
                                     IPerformantaLivrariAdapter performantaAdapter,
                                     IJurnalAudit jurnalAudit,
                                     AlocareInspectorService alocareService,
                                     CalculScorecardService scorecardService) {
        this.furnizorRepo = furnizorRepo;
        this.inspectorRepo = inspectorRepo;
        this.inspectieRepo = inspectieRepo;
        this.ncrRepo = ncrRepo;
        this.outboxRepo = outboxRepo;
        this.inboxRepo = inboxRepo;
        this.performantaAdapter = performantaAdapter;
        this.jurnalAudit = jurnalAudit;
        this.alocareService = alocareService;
        this.scorecardService = scorecardService;
    }

    public IdFurnizor inregistreazaFurnizor(String codFiscal, String denumire, Adresa adresa,
                                           Set<Email> emailuri, Iban iban, Set<String> categorii,
                                           ConditiiContractuale conditii) {
        IdFurnizor id = IdFurnizor.random();
        Furnizor f = new Furnizor(id, codFiscal, denumire, adresa, emailuri, iban, categorii, conditii);
        furnizorRepo.add(f);
        jurnalAudit.inregistreazaAcces("INREGISTREAZA_FURNIZOR", "Furnizor: " + denumire + " CUI: " + codFiscal);
        return id;
    }

    public void actualizeazaProfilFurnizor(IdFurnizor id, String denumire, Adresa adresa,
                                          Set<Email> emailuri, Iban iban, Set<String> categorii,
                                          ConditiiContractuale conditii) {
        Furnizor f = furnizorRepo.findById(id)
            .orElseThrow(() -> new NoSuchElementException("Furnizorul nu a fost gasit: " + id.getValue()));
        f.actualizeazaProfil(denumire, adresa, emailuri, iban, categorii, conditii);
        furnizorRepo.save(f);
        jurnalAudit.inregistreazaAcces("ACTUALIZEAZA_PROFIL_FURNIZOR", "Furnizor: " + id.getValue());
    }

    public void calificaFurnizor(IdFurnizor id, String decizie, String motiv) {
        Furnizor f = furnizorRepo.findById(id)
            .orElseThrow(() -> new NoSuchElementException("Furnizorul nu a fost gasit: " + id.getValue()));
        
        switch (decizie.toUpperCase()) {
            case "EVALUARE" -> f.trimiteLaEvaluare();
            case "APROBA" -> f.aproba();
            case "SUSPENDA" -> {
                f.suspenda(motiv);
                outboxRepo.saveEvent(new FurnizorSuspendatEvent(UUID.randomUUID(), Instant.now(), id, motiv));
                outboxRepo.saveEvent(new ro.uaic.feaa.sdbis.supplychain.calitate.api.event.FurnizorSuspendatPublicEvent(
                    UUID.randomUUID(), Instant.now(), id, motiv
                ));
            }
            case "BLOCHEA" -> {
                FurnizorBlocatEvent evt = f.blocheaza(motiv);
                outboxRepo.saveEvent(evt);
                outboxRepo.saveEvent(new ro.uaic.feaa.sdbis.supplychain.calitate.api.event.FurnizorBlocatPublicEvent(
                    UUID.randomUUID(), evt.occurredOn(), id.getValue(), motiv
                ));
            }
            default -> throw new IllegalArgumentException("Decizie calificare necunoscuta: " + decizie);
        }
        furnizorRepo.save(f);
        jurnalAudit.inregistreazaAcces("CALIFICA_FURNIZOR", "Furnizor: " + id.getValue() + " Decizie: " + decizie);
    }

    public UUID creeazaInspector(String firstName, String lastName) {
        UUID id = UUID.randomUUID();
        InspectorCalitate insp = new InspectorCalitate(id, new FullName(firstName, lastName));
        inspectorRepo.add(insp);
        jurnalAudit.inregistreazaAcces("CREEAZA_INSPECTOR", "Inspector: " + firstName + " " + lastName);
        return id;
    }

    public void adaugaCertificareInspector(UUID inspectorId, String denumire, LocalDate expirare) {
        InspectorCalitate insp = inspectorRepo.findById(inspectorId)
            .orElseThrow(() -> new NoSuchElementException("Inspectorul nu a fost gasit: " + inspectorId));
        insp.adaugaCertificare(new Certificare(denumire, expirare));
        inspectorRepo.save(insp);
        jurnalAudit.inregistreazaAcces("ADAUGA_CERTIFICARE", "Inspector: " + inspectorId + " Certificare: " + denumire);
    }

    public void declaraDisponibilitateInspector(UUID inspectorId, LocalDate start, LocalDate stop) {
        InspectorCalitate insp = inspectorRepo.findById(inspectorId)
            .orElseThrow(() -> new NoSuchElementException("Inspectorul nu a fost gasit: " + inspectorId));
        insp.declaraDisponibilitate(new Perioada(start, stop));
        inspectorRepo.save(insp);
    }

    public UUID programeazaInspectie(IdLot lotId, IdComanda comandaId, IdFurnizor furnizorId,
                                      CodMaterial material, PlanAQL plan, Perioada p) {
        InspectieCalitate ins = InspectieCalitate.programeaza(lotId, comandaId, furnizorId, material, plan, p);
        inspectieRepo.add(ins);
        outboxRepo.saveEvent(new InspectieProgramataEvent(
            UUID.randomUUID(), Instant.now(), ins.getIdInspectie(), lotId, comandaId
        ));
        return ins.getIdInspectie();
    }

    public void alocaInspector(AlocaInspectorCommand cmd) {
        InspectieCalitate ins = inspectieRepo.findById(cmd.inspectieId())
            .orElseThrow(() -> new NoSuchElementException("Inspectia nu a fost gasita: " + cmd.inspectieId()));
        InspectorCalitate insp = inspectorRepo.findById(cmd.inspectorId())
            .orElseThrow(() -> new NoSuchElementException("Inspectorul nu a fost gasit: " + cmd.inspectorId()));

        alocareService.aloca(ins, insp);
        inspectieRepo.save(ins);
    }

    public void incepeInspectie(IncepeInspectieCommand cmd) {
        InspectieCalitate ins = inspectieRepo.findById(cmd.inspectieId())
            .orElseThrow(() -> new NoSuchElementException("Inspectia nu a fost gasita: " + cmd.inspectieId()));
        ins.incepe();
        inspectieRepo.save(ins);
    }

    public void inregistreazaVerdict(InregistreazaVerdictCommand cmd) {
        InspectieCalitate ins = inspectieRepo.findById(cmd.inspectieId())
            .orElseThrow(() -> new NoSuchElementException("Inspectia nu a fost gasita: " + cmd.inspectieId()));
        VerdictInspectieEvent event = ins.inregistreazaVerdict(cmd.defecteGasite());
        ins.inchide();
        inspectieRepo.save(ins);

        Furnizor furn = furnizorRepo.findById(ins.getFurnizorId())
            .orElseThrow(() -> new NoSuchElementException("Furnizorul nu a fost gasit: " + ins.getFurnizorId()));

        FurnizorSuspendatEvent suspEvent = furn.inregistreazaRezultatInspectie(ins.getVerdict());
        furnizorRepo.save(furn);

        if (ins.getVerdict() == Verdict.Neconforma) {
            RaportNeconformitate ncr = new RaportNeconformitate(UUID.randomUUID(), ins.getIdInspectie(), ins.getFurnizorId(), ins.getLotId());
            ncrRepo.add(ncr);
        }

        if (suspEvent != null) {
            outboxRepo.saveEvent(suspEvent);
            outboxRepo.saveEvent(new ro.uaic.feaa.sdbis.supplychain.calitate.api.event.FurnizorSuspendatPublicEvent(
                UUID.randomUUID(), suspEvent.occurredOn(), suspEvent.furnizorId(), suspEvent.motiv()
            ));
        }
        outboxRepo.saveEvent(event);
        outboxRepo.saveEvent(new ro.uaic.feaa.sdbis.supplychain.calitate.api.event.VerdictInspectiePublicEvent(
            UUID.randomUUID(),
            event.occurredOn(),
            ins.getIdInspectie(),
            event.lotId(),
            event.comandaId(),
            ins.getVerdict().name(),
            ins.getVerdict() == Verdict.Conforma
        ));
        jurnalAudit.inregistreazaAcces("INREGISTRARE_VERDICT", "Inspectie: " + cmd.inspectieId() + " -> " + ins.getVerdict());
    }

    public void solicitaReinspectie(UUID inspectieId) {
        InspectieCalitate ins = inspectieRepo.findById(inspectieId)
            .orElseThrow(() -> new NoSuchElementException("Inspectia nu a fost gasita: " + inspectieId));
        ins.solicitaReinspectie();
        inspectieRepo.save(ins);
    }

    public void analizeazaCauzaNCR(UUID ncrId, String cauza) {
        RaportNeconformitate ncr = ncrRepo.findById(ncrId)
            .orElseThrow(() -> new NoSuchElementException("NCR nu a fost gasit: " + ncrId));
        ncr.analizeazaCauza(cauza);
        ncrRepo.save(ncr);
    }

    public void initiazaActiuneNCR(UUID ncrId, String descriere, String responsabil, LocalDate termen) {
        RaportNeconformitate ncr = ncrRepo.findById(ncrId)
            .orElseThrow(() -> new NoSuchElementException("NCR nu a fost gasit: " + ncrId));
        ncr.initiazaActiuneCorectiva(new ActiuneCorectiva(descriere, responsabil, termen));
        ncrRepo.save(ncr);
    }

    public void verificaNCR(UUID ncrId) {
        RaportNeconformitate ncr = ncrRepo.findById(ncrId)
            .orElseThrow(() -> new NoSuchElementException("NCR nu a fost gasit: " + ncrId));
        ncr.verifica();
        ncrRepo.save(ncr);
    }

    public void inchideNCR(UUID ncrId) {
        RaportNeconformitate ncr = ncrRepo.findById(ncrId)
            .orElseThrow(() -> new NoSuchElementException("NCR nu a fost gasit: " + ncrId));
        ncr.inchide();
        ncrRepo.save(ncr);
    }

    public void escaladeazaNCR(UUID ncrId) {
        RaportNeconformitate ncr = ncrRepo.findById(ncrId)
            .orElseThrow(() -> new NoSuchElementException("NCR nu a fost gasit: " + ncrId));
        NCREscaladatEvent evt = ncr.escaladeaza();
        ncrRepo.save(ncr);
        outboxRepo.saveEvent(evt);
    }

    public void calculeazaScorecardLunar(CalculScorecardCommand cmd) {
        Furnizor furn = furnizorRepo.findById(cmd.furnizorId())
            .orElseThrow(() -> new NoSuchElementException("Furnizor not found: " + cmd.furnizorId()));

        PerformantaLivrariVO perf = performantaAdapter.obtinePerformanta(cmd.furnizorId(), cmd.luna());
        ScorecardLunar sc = scorecardService.calculeazaScorecard(cmd.luna(), perf, cmd.rataRespingere());

        furn.aplicaScorecard(sc);
        furnizorRepo.save(furn);
        jurnalAudit.inregistreazaAcces("CALCUL_SCORECARD", "Furnizor: " + cmd.furnizorId() + " -> " + sc.calificativ());
    }

    @Transactional(readOnly = true)
    public boolean esteFurnizorEligibil(IdFurnizor id) {
        return furnizorRepo.findById(id)
            .map(Furnizor::esteEligibilPentruComenzi)
            .orElse(false);
    }
}
