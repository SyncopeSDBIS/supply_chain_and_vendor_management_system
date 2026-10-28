package ro.uaic.feaa.sdbis.supplychain.facturi.application;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ro.uaic.feaa.sdbis.supplychain.facturi.application.command.*;
import ro.uaic.feaa.sdbis.supplychain.facturi.domain.*;
import ro.uaic.feaa.sdbis.supplychain.facturi.domain.event.PlataExecutataEvent;
import ro.uaic.feaa.sdbis.supplychain.shared.*;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

import ro.uaic.feaa.sdbis.supplychain.shared.annotation.ApplicationService;

@Service
@Transactional
@ApplicationService
public class FacturaApplicationService implements IFacturaWorkflowService {

    private final FacturaRepo facturaRepo;
    private final NIRRepo nirRepo;
    private final OrdinPlataRepo ordinRepo;
    private final FurnizoriBlocatiRepo furnizoriBlocatiRepo;
    private final OutboxRepo outboxRepo;
    private final InboxRepo inboxRepo;
    private final IComenziAdapter comenziAdapter;
    private final IJurnalAudit jurnalAudit;
    private final ReconciliereService reconciliereService;
    private final IBancaPlataProvider bancaProvider;

    public FacturaApplicationService(FacturaRepo facturaRepo,
                                     NIRRepo nirRepo,
                                     OrdinPlataRepo ordinRepo,
                                     FurnizoriBlocatiRepo furnizoriBlocatiRepo,
                                     OutboxRepo outboxRepo,
                                     InboxRepo inboxRepo,
                                     IComenziAdapter comenziAdapter,
                                     IJurnalAudit jurnalAudit,
                                     ReconciliereService reconciliereService,
                                     IBancaPlataProvider bancaProvider) {
        this.facturaRepo = facturaRepo;
        this.nirRepo = nirRepo;
        this.ordinRepo = ordinRepo;
        this.furnizoriBlocatiRepo = furnizoriBlocatiRepo;
        this.outboxRepo = outboxRepo;
        this.inboxRepo = inboxRepo;
        this.comenziAdapter = comenziAdapter;
        this.jurnalAudit = jurnalAudit;
        this.reconciliereService = reconciliereService;
        this.bancaProvider = bancaProvider;
    }

    public UUID inregistreazaFactura(InregistreazaFacturaCommand cmd) {
        UUID id = UUID.randomUUID();
        FacturaFurnizor f = new FacturaFurnizor(
            id, cmd.serie(), cmd.numar(), cmd.dataEmitere(), cmd.dataScadenta(),
            cmd.comandaId(), cmd.furnizorId(), cmd.linii(), cmd.moneda()
        );
        facturaRepo.add(f);
        jurnalAudit.inregistreazaAcces("INREGISTREAZA_FACTURA", "Factura: " + cmd.serie() + " " + cmd.numar());
        return id;
    }

    public void contestaFactura(UUID facturaId, String motiv) {
        FacturaFurnizor f = facturaRepo.findById(facturaId)
            .orElseThrow(() -> new NoSuchElementException("Factura nu a fost gasita: " + facturaId));
        f.contesta(motiv);
        facturaRepo.save(f);
        jurnalAudit.inregistreazaAcces("CONTESTA_FACTURA", "Factura: " + facturaId + " Motiv: " + motiv);
    }

    public void genereazaNIR(GenereazaNIRCommand cmd) {
        if (nirRepo.existaNIRPentruEvent(cmd.eventSursaId())) {
            return;
        }

        NIR n = new NIR(UUID.randomUUID(), cmd.numarNIR(), cmd.comandaId(), cmd.eventSursaId(), cmd.dataReceptie(), cmd.linii());
        nirRepo.add(n);
        jurnalAudit.inregistreazaAcces("GENEREAZA_NIR", "NIR: " + cmd.numarNIR());
    }

    public void contabilizeazaNIR(UUID nirId) {
        NIR n = nirRepo.findById(nirId)
            .orElseThrow(() -> new NoSuchElementException("NIR nu a fost gasit: " + nirId));
        n.contabilizeaza();
        nirRepo.save(n);
        jurnalAudit.inregistreazaAcces("CONTABILIZEAZA_NIR", "NIR: " + nirId);
    }

    public void storneazaNIR(UUID nirId, String motiv) {
        NIR n = nirRepo.findById(nirId)
            .orElseThrow(() -> new NoSuchElementException("NIR nu a fost gasit: " + nirId));
        n.storneaza(motiv);
        nirRepo.save(n);
        jurnalAudit.inregistreazaAcces("STORNEAZA_NIR", "NIR: " + nirId + " Motiv: " + motiv);
    }

    public void reconcilieazaFactura(ReconciliereCommand cmd) {
        FacturaFurnizor f = facturaRepo.findById(cmd.facturaId())
            .orElseThrow(() -> new NoSuchElementException("Factura nu a fost gasita: " + cmd.facturaId()));

        List<NIR> niruri = nirRepo.findByComandaId(f.getComandaId());
        if (niruri.isEmpty()) {
            throw new IllegalStateException("Nu exista niciun NIR generat pentru comanda " + f.getComandaId());
        }
        NIR nir = niruri.get(0);

        ComandaSummaryVO comanda = comenziAdapter.obtineDateComanda(f.getComandaId());

        f.trimiteLaVerificare();
        nir.trimiteLaReconciliere();

        RezultatReconciliere res = reconciliereService.reconciliaza(f, nir, comanda, Toleranta.standard());

        if (res.aprobata()) {
            f.aproba();
            nir.reconciliaza();
        } else {
            f.blocheaza(String.join("; ", res.diferente()));
            nir.marcheazaDiscrepante();
        }

        facturaRepo.save(f);
        nirRepo.save(nir);
        jurnalAudit.inregistreazaAcces("RECONCILIERE_FACTURA", "Factura: " + cmd.facturaId() + " -> " + (res.aprobata() ? "Aprobata" : "Blocata"));
    }

    public UUID creeazaOrdinPlata(CreeazaOrdinPlataCommand cmd) {
        FacturaFurnizor f = facturaRepo.findById(cmd.facturaId())
            .orElseThrow(() -> new NoSuchElementException("Factura nu a fost gasita: " + cmd.facturaId()));

        if (furnizoriBlocatiRepo.esteBlocat(f.getFurnizorId())) {
            throw new IllegalStateException("Furnizorul este blocat. Platile sunt oprite!");
        }

        OrdinPlata ordin = f.creeazaOrdinPlata(cmd.suma(), cmd.creatDe());
        ordinRepo.add(ordin);
        jurnalAudit.inregistreazaAcces("CREEAZA_ORDIN_PLATA", "Ordin: " + ordin.getIdOrdin() + " suma: " + cmd.suma());
        return ordin.getIdOrdin();
    }

    public void contestaFactura(ContestaFacturaCommand cmd) {
        contestaFactura(cmd.facturaId(), cmd.motiv());
    }

    public void contabilizeazaNIR(ContabilizeazaNIRCommand cmd) {
        contabilizeazaNIR(cmd.nirId());
    }

    public void storneazaNIR(StorneazaNIRCommand cmd) {
        storneazaNIR(cmd.nirId(), cmd.motiv());
    }

    public void aprobaOrdin(UUID ordinId, UUID aprobatorId) {
        OrdinPlata ordin = ordinRepo.findById(ordinId)
            .orElseThrow(() -> new NoSuchElementException("Ordinul nu a fost gasit: " + ordinId));
        ordin.aproba(aprobatorId);
        ordinRepo.save(ordin);
    }

    public void aprobaOrdin(AprobaOrdinCommand cmd) {
        aprobaOrdin(cmd.ordinId(), cmd.aprobatorId());
    }

    public void transmiteOrdin(TransmiteOrdinCommand cmd) {
        OrdinPlata ordin = ordinRepo.findById(cmd.ordinId())
            .orElseThrow(() -> new NoSuchElementException("Ordinul nu a fost gasit: " + cmd.ordinId()));

        FacturaFurnizor f = facturaRepo.findById(ordin.getFacturaId())
            .orElseThrow(() -> new NoSuchElementException("Factura nu a fost gasita: " + ordin.getFacturaId()));

        if (furnizoriBlocatiRepo.esteBlocat(f.getFurnizorId())) {
            ordin.anuleaza();
            ordinRepo.save(ordin);
            throw new IllegalStateException("Furnizorul este blocat. Ordinul a fost anulat!");
        }

        // Daca ordinul are suma <= 10.000 EUR si este in starea Creat, Ofiterul Financiar il autorizeaza automat
        Suma prag10k = Suma.of(10000.0, ordin.getSuma().getMoneda().getCurrencyCode());
        if (ordin.getStareOrdin() == StareOrdinPlata.Creat && !ordin.getSuma().esteMaiMareDecat(prag10k)) {
            UUID ofiterId = cmd.ofiterId() != null ? cmd.ofiterId() : UUID.nameUUIDFromBytes(("OFITER_FINANCIAR_" + ordin.getIdOrdin()).getBytes());
            if (ofiterId.equals(ordin.getCreatDe())) {
                ofiterId = UUID.randomUUID();
            }
            ordin.aproba(ofiterId);
        }

        ordin.transmiteLaBanca();
        ordinRepo.save(ordin);

        bancaProvider.transmiteOrdinPlata(ordin, cmd.ibanFurnizor());
    }

    public void inregistreazaExecutiePlata(UUID ordinId) {
        OrdinPlata ordin = ordinRepo.findById(ordinId)
            .orElseThrow(() -> new NoSuchElementException("Ordinul nu a fost gasit: " + ordinId));
        PlataExecutataEvent event = ordin.inregistreazaExecutie();
        ordinRepo.save(ordin);

        FacturaFurnizor f = facturaRepo.findById(ordin.getFacturaId())
            .orElseThrow(() -> new NoSuchElementException("Factura nu a fost gasita: " + ordin.getFacturaId()));
        f.inregistreazaPlata(ordin.getSuma());
        facturaRepo.save(f);

        outboxRepo.saveEvent(event);
        outboxRepo.saveEvent(new ro.uaic.feaa.sdbis.supplychain.facturi.api.event.PlataExecutataPublicEvent(
            UUID.randomUUID(),
            event.occurredOn(),
            event.ordinId(),
            ordin.getFacturaId(),
            event.suma().getValoare(),
            event.suma().getMoneda().getCurrencyCode()
        ));
        jurnalAudit.inregistreazaAcces("EXECUTIE_PLATA", "Ordin: " + ordinId + " executat cu succes");
    }

    public void inregistreazaExecutiePlata(InregistreazaExecutiePlataCommand cmd) {
        inregistreazaExecutiePlata(cmd.ordinId());
    }

    public void respingeDeBanca(RespingereBancaCommand cmd) {
        OrdinPlata ordin = ordinRepo.findById(cmd.ordinId())
            .orElseThrow(() -> new NoSuchElementException("Ordinul nu a fost gasit: " + cmd.ordinId()));
        ordin.respingeDeBanca(cmd.motiv());
        ordinRepo.save(ordin);
        jurnalAudit.inregistreazaAcces("RESPINGERE_BANCA", "Ordin: " + cmd.ordinId() + " motiv: " + cmd.motiv());
    }

    public void blocheazaFacturiComandaAnulata(IdComanda comandaId) {
        List<FacturaFurnizor> facturi = facturaRepo.findAll().stream()
            .filter(f -> f.getComandaId().equals(comandaId))
            .toList();
        for (FacturaFurnizor f : facturi) {
            f.anuleaza();
            facturaRepo.save(f);
        }
        jurnalAudit.inregistreazaAcces("ANULARE_FACTURI_COMANDA_ANULATA", "Comanda: " + comandaId.getValue() + " (facturi anulate: " + facturi.size() + ")");
    }

    public void blocheazaPlatiFurnizor(IdFurnizor furnizorId) {
        furnizoriBlocatiRepo.adauga(furnizorId);
        jurnalAudit.inregistreazaAcces("BLOCARE_PLATI_FURNIZOR", "Furnizor: " + furnizorId);
    }

    @Override
    public void inregistreazaPlataFactura(UUID facturaId, Suma suma) {
        FacturaFurnizor f = facturaRepo.findById(facturaId)
            .orElseThrow(() -> new NoSuchElementException("Factura nu a fost gasita: " + facturaId));
        f.inregistreazaPlata(suma);
        facturaRepo.save(f);
        jurnalAudit.inregistreazaAcces("INREGISTREAZA_PLATA_FACTURA", "Factura: " + facturaId + " suma: " + suma);
    }
}

