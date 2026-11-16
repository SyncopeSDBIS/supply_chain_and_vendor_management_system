package ro.uaic.feaa.sdbis.supplychain.facturi.application;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;
import ro.uaic.feaa.sdbis.supplychain.facturi.api.event.PlataExecutataPublicEvent;
import ro.uaic.feaa.sdbis.supplychain.facturi.application.command.AprobaOrdinCommand;
import ro.uaic.feaa.sdbis.supplychain.facturi.application.command.ContabilizeazaNIRCommand;
import ro.uaic.feaa.sdbis.supplychain.facturi.application.command.ContestaFacturaCommand;
import ro.uaic.feaa.sdbis.supplychain.facturi.application.command.CreeazaOrdinPlataCommand;
import ro.uaic.feaa.sdbis.supplychain.facturi.application.command.GenereazaNIRCommand;
import ro.uaic.feaa.sdbis.supplychain.facturi.application.command.InregistreazaExecutiePlataCommand;
import ro.uaic.feaa.sdbis.supplychain.facturi.application.command.InregistreazaFacturaCommand;
import ro.uaic.feaa.sdbis.supplychain.facturi.application.command.ReconciliereCommand;
import ro.uaic.feaa.sdbis.supplychain.facturi.application.command.RespingereBancaCommand;
import ro.uaic.feaa.sdbis.supplychain.facturi.application.command.StorneazaNIRCommand;
import ro.uaic.feaa.sdbis.supplychain.facturi.application.command.TransmiteOrdinCommand;
import ro.uaic.feaa.sdbis.supplychain.facturi.domain.ComandaSummaryVO;
import ro.uaic.feaa.sdbis.supplychain.facturi.domain.FacturaFurnizor;
import ro.uaic.feaa.sdbis.supplychain.facturi.domain.FacturaRepo;
import ro.uaic.feaa.sdbis.supplychain.facturi.domain.FurnizoriBlocatiRepo;
import ro.uaic.feaa.sdbis.supplychain.facturi.domain.IBancaPlataProvider;
import ro.uaic.feaa.sdbis.supplychain.facturi.domain.IComenziAdapter;
import ro.uaic.feaa.sdbis.supplychain.facturi.domain.NIR;
import ro.uaic.feaa.sdbis.supplychain.facturi.domain.NIRRepo;
import ro.uaic.feaa.sdbis.supplychain.facturi.domain.OrdinPlata;
import ro.uaic.feaa.sdbis.supplychain.facturi.domain.OrdinPlataRepo;
import ro.uaic.feaa.sdbis.supplychain.facturi.domain.ReconciliereService;
import ro.uaic.feaa.sdbis.supplychain.facturi.domain.RezultatReconciliere;
import ro.uaic.feaa.sdbis.supplychain.facturi.domain.StareFactura;
import ro.uaic.feaa.sdbis.supplychain.facturi.domain.StareNIR;
import ro.uaic.feaa.sdbis.supplychain.facturi.domain.StareOrdinPlata;
import ro.uaic.feaa.sdbis.supplychain.facturi.domain.Suma;
import ro.uaic.feaa.sdbis.supplychain.facturi.domain.Toleranta;
import ro.uaic.feaa.sdbis.supplychain.facturi.domain.event.PlataExecutataEvent;
import ro.uaic.feaa.sdbis.supplychain.shared.IJurnalAudit;
import ro.uaic.feaa.sdbis.supplychain.shared.IdComanda;
import ro.uaic.feaa.sdbis.supplychain.shared.IdFurnizor;
import ro.uaic.feaa.sdbis.supplychain.shared.InboxRepo;
import ro.uaic.feaa.sdbis.supplychain.shared.OutboxRepo;
import ro.uaic.feaa.sdbis.supplychain.shared.annotation.ApplicationService;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
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
    private final TransactionTemplate transactionTemplate;

    public FacturaApplicationService(FacturaRepo facturaRepo,
                                     NIRRepo nirRepo,
                                     OrdinPlataRepo ordinRepo,
                                     FurnizoriBlocatiRepo furnizoriBlocatiRepo,
                                     OutboxRepo outboxRepo,
                                     InboxRepo inboxRepo,
                                     IComenziAdapter comenziAdapter,
                                     IJurnalAudit jurnalAudit,
                                     ReconciliereService reconciliereService,
                                     IBancaPlataProvider bancaProvider,
                                     @Autowired(required = false) TransactionTemplate transactionTemplate) {
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
        this.transactionTemplate = transactionTemplate;
    }

    @Transactional
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

    @Transactional
    public void contestaFactura(UUID facturaId, String motiv) {
        FacturaFurnizor f = facturaRepo.findById(facturaId)
            .orElseThrow(() -> new NoSuchElementException("Factura nu a fost gasita: " + facturaId));
        f.contesta(motiv);
        facturaRepo.save(f);
        jurnalAudit.inregistreazaAcces("CONTESTA_FACTURA", "Factura: " + facturaId + " Motiv: " + motiv);
    }

    @Transactional
    public void genereazaNIR(GenereazaNIRCommand cmd) {
        if (nirRepo.existaNIRPentruEvent(cmd.eventSursaId())) {
            return;
        }

        NIR n = new NIR(UUID.randomUUID(), cmd.numarNIR(), cmd.comandaId(), cmd.eventSursaId(), cmd.dataReceptie(), cmd.linii());
        nirRepo.add(n);
        jurnalAudit.inregistreazaAcces("GENEREAZA_NIR", "NIR: " + cmd.numarNIR());
    }

    @Transactional
    public void contabilizeazaNIR(UUID nirId) {
        NIR n = nirRepo.findById(nirId)
            .orElseThrow(() -> new NoSuchElementException("NIR nu a fost gasit: " + nirId));
        n.contabilizeaza();
        nirRepo.save(n);
        jurnalAudit.inregistreazaAcces("CONTABILIZEAZA_NIR", "NIR: " + nirId);
    }

    @Transactional
    public void storneazaNIR(UUID nirId, String motiv) {
        NIR n = nirRepo.findById(nirId)
            .orElseThrow(() -> new NoSuchElementException("NIR nu a fost gasit: " + nirId));
        n.storneaza(motiv);
        nirRepo.save(n);
        jurnalAudit.inregistreazaAcces("STORNEAZA_NIR", "NIR: " + nirId + " Motiv: " + motiv);
    }

    @Transactional
    public void reconcilieazaFactura(ReconciliereCommand cmd) {
        FacturaFurnizor f = facturaRepo.findById(cmd.facturaId())
            .orElseThrow(() -> new NoSuchElementException("Factura nu a fost gasita: " + cmd.facturaId()));

        List<NIR> listaNiruri = new ArrayList<>();
        if (cmd.nirIds() != null && !cmd.nirIds().isEmpty()) {
            for (UUID nid : cmd.nirIds()) {
                NIR n = nirRepo.findById(nid)
                    .orElseThrow(() -> new NoSuchElementException("NIR-ul nu a fost gasit: " + nid));
                if (!n.getComandaId().equals(f.getComandaId())) {
                    throw new IllegalArgumentException("NIR-ul " + nid + " nu apartine comenzii " + f.getComandaId());
                }
                listaNiruri.add(n);
            }
        } else if (cmd.nirId() != null) {
            NIR n = nirRepo.findById(cmd.nirId())
                .orElseThrow(() -> new NoSuchElementException("NIR-ul nu a fost gasit: " + cmd.nirId()));
            if (!n.getComandaId().equals(f.getComandaId())) {
                throw new IllegalArgumentException("NIR-ul " + cmd.nirId() + " nu apartine comenzii " + f.getComandaId());
            }
            listaNiruri.add(n);
        } else {
            List<NIR> niruri = nirRepo.findByComandaId(f.getComandaId());
            if (niruri.isEmpty()) {
                throw new IllegalStateException("Nu exista niciun NIR generat pentru comanda " + f.getComandaId());
            }
            List<NIR> activeNiruri = niruri.stream()
                .filter(n -> n.getStareNIR() != StareNIR.Reconciliat &&
                             n.getStareNIR() != StareNIR.Contabilizat &&
                             n.getStareNIR() != StareNIR.Stornat)
                .toList();
            if (activeNiruri.isEmpty()) {
                listaNiruri.add(niruri.get(0));
            } else {
                listaNiruri.addAll(activeNiruri);
            }
        }

        // FIX TEST-01: Protectie impotriva facturarii duplicate pe acelasi NIR
        for (NIR nir : listaNiruri) {
            if (nir.getStareNIR() == StareNIR.Reconciliat || nir.getStareNIR() == StareNIR.Contabilizat) {
                List<FacturaFurnizor> facturiExistente = facturaRepo.findByComandaId(f.getComandaId());
                boolean hasActiveApprovedInvoice = facturiExistente.stream()
                    .anyMatch(fe -> !fe.getIdFactura().equals(f.getIdFactura()) &&
                            (fe.getStareFactura() == StareFactura.AprobataLaPlata ||
                             fe.getStareFactura() == StareFactura.Platita ||
                             fe.getStareFactura() == StareFactura.PartialPlatita));
                if (hasActiveApprovedInvoice) {
                    f.blocheaza("NIR-ul " + nir.getNumarNIR() + " a fost deja reconciliat si facturat.");
                    facturaRepo.save(f);
                    throw new IllegalStateException("Factura duplicata: NIR-ul pentru comanda " + f.getComandaId() +
                            " a fost deja reconciliat si aprobat anterior.");
                }
            }
        }

        ComandaSummaryVO comanda = comenziAdapter.obtineDateComanda(f.getComandaId());

        f.trimiteLaVerificare();
        for (NIR nir : listaNiruri) {
            nir.trimiteLaReconciliere();
        }

        RezultatReconciliere res = reconciliereService.reconciliaza(f, listaNiruri, comanda, Toleranta.standard());

        if (res.aprobata()) {
            f.aproba();
            for (NIR nir : listaNiruri) {
                nir.reconciliaza();
            }
        } else {
            f.blocheaza(String.join("; ", res.diferente()));
            for (NIR nir : listaNiruri) {
                nir.marcheazaDiscrepante();
            }
        }

        facturaRepo.save(f);
        for (NIR nir : listaNiruri) {
            nirRepo.save(nir);
        }
        jurnalAudit.inregistreazaAcces("RECONCILIERE_FACTURA", "Factura: " + cmd.facturaId() + " -> " + (res.aprobata() ? "Aprobata" : "Blocata") + " (NIR-uri: " + listaNiruri.size() + ")");
    }

    @Transactional
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

    @Transactional
    public void contestaFactura(ContestaFacturaCommand cmd) {
        contestaFactura(cmd.facturaId(), cmd.motiv());
    }

    @Transactional
    public void contabilizeazaNIR(ContabilizeazaNIRCommand cmd) {
        contabilizeazaNIR(cmd.nirId());
    }

    @Transactional
    public void storneazaNIR(StorneazaNIRCommand cmd) {
        storneazaNIR(cmd.nirId(), cmd.motiv());
    }

    @Transactional
    public void aprobaOrdin(UUID ordinId, UUID aprobatorId) {
        OrdinPlata ordin = ordinRepo.findById(ordinId)
            .orElseThrow(() -> new NoSuchElementException("Ordinul nu a fost gasit: " + ordinId));
        ordin.aproba(aprobatorId);
        ordinRepo.save(ordin);
    }

    @Transactional
    public void aprobaOrdin(AprobaOrdinCommand cmd) {
        aprobaOrdin(cmd.ordinId(), cmd.aprobatorId());
    }

    public void transmiteOrdin(TransmiteOrdinCommand cmd) {
        AtomicBoolean furnizorBlocat = new AtomicBoolean(false);
        // Pasul 1 (Tranzactionat scurt): validare, aprobare <=10k si marcare 'transmiteLaBanca'
        OrdinPlata ordin = (transactionTemplate != null)
                ? transactionTemplate.execute(status -> pregatesteSiMarcheazaTransmitere(cmd, furnizorBlocat))
                : pregatesteSiMarcheazaTransmitere(cmd, furnizorBlocat);

        if (furnizorBlocat.get()) {
            throw new IllegalStateException("Furnizorul este blocat. Ordinul a fost anulat!");
        }

        // Pasul 2 (NON-TRANZACTIONAT in productie): Conexiunea JDBC HikariCP este eliberata in pool in timpul apelului extern Stripe!
        boolean transmis = bancaProvider.transmiteOrdinPlata(ordin, cmd.ibanFurnizor());

        // Pasul 3 (Tranzactionat scurt): Daca gateway-ul a respins transmiterea, marcare 'respingeDeBanca'
        if (!transmis) {
            if (transactionTemplate != null) {
                transactionTemplate.executeWithoutResult(status -> {
                    OrdinPlata o = ordinRepo.findById(cmd.ordinId()).orElse(ordin);
                    o.respingeDeBanca("Eroare transmitere gateway bancar");
                    ordinRepo.save(o);
                });
            } else {
                ordin.respingeDeBanca("Eroare transmitere gateway bancar");
                ordinRepo.save(ordin);
            }
        }
    }

    private OrdinPlata pregatesteSiMarcheazaTransmitere(TransmiteOrdinCommand cmd, AtomicBoolean furnizorBlocat) {
        OrdinPlata ordin = ordinRepo.findById(cmd.ordinId())
            .orElseThrow(() -> new NoSuchElementException("Ordinul nu a fost gasit: " + cmd.ordinId()));

        FacturaFurnizor f = facturaRepo.findById(ordin.getFacturaId())
            .orElseThrow(() -> new NoSuchElementException("Factura nu a fost gasita: " + ordin.getFacturaId()));

        if (furnizoriBlocatiRepo.esteBlocat(f.getFurnizorId())) {
            ordin.anuleaza();
            ordinRepo.save(ordin);
            furnizorBlocat.set(true);
            return ordin;
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
        return ordin;
    }

    @Transactional
    public void inregistreazaExecutiePlata(UUID ordinId) {
        OrdinPlata ordin = ordinRepo.findById(ordinId)
            .orElseThrow(() -> new NoSuchElementException("Ordinul nu a fost gasit: " + ordinId));
        if (ordin.getStareOrdin() == StareOrdinPlata.Executat) {
            jurnalAudit.inregistreazaAcces("EXECUTIE_PLATA_DUPLICAT", "Ordin: " + ordinId + " deja executat. Apel duplicat ignorat (idempotenta).");
            return;
        }
        PlataExecutataEvent event = ordin.inregistreazaExecutie();
        ordinRepo.save(ordin);

        FacturaFurnizor f = facturaRepo.findById(ordin.getFacturaId())
            .orElseThrow(() -> new NoSuchElementException("Factura nu a fost gasita: " + ordin.getFacturaId()));
        f.inregistreazaPlata(ordin.getSuma());
        facturaRepo.save(f);

        outboxRepo.saveEvent(event);
        outboxRepo.saveEvent(new PlataExecutataPublicEvent(
            UUID.randomUUID(),
            event.occurredOn(),
            event.ordinId(),
            ordin.getFacturaId(),
            event.suma().getValoare(),
            event.suma().getMoneda().getCurrencyCode()
        ));
        jurnalAudit.inregistreazaAcces("EXECUTIE_PLATA", "Ordin: " + ordinId + " executat cu succes");
    }

    @Transactional
    public void inregistreazaExecutiePlata(InregistreazaExecutiePlataCommand cmd) {
        inregistreazaExecutiePlata(cmd.ordinId());
    }

    @Transactional
    public void respingeDeBanca(RespingereBancaCommand cmd) {
        OrdinPlata ordin = ordinRepo.findById(cmd.ordinId())
            .orElseThrow(() -> new NoSuchElementException("Ordinul nu a fost gasit: " + cmd.ordinId()));
        if (ordin.getStareOrdin() == StareOrdinPlata.RespinsDeBanca) {
            jurnalAudit.inregistreazaAcces("RESPINGERE_BANCA_DUPLICAT", "Ordin: " + cmd.ordinId() + " deja respins. Apel duplicat ignorat.");
            return;
        }
        ordin.respingeDeBanca(cmd.motiv());
        ordinRepo.save(ordin);
        jurnalAudit.inregistreazaAcces("RESPINGERE_BANCA", "Ordin: " + cmd.ordinId() + " motiv: " + cmd.motiv());
    }

    @Transactional
    public void blocheazaFacturiComandaAnulata(IdComanda comandaId) {
        List<FacturaFurnizor> facturi = facturaRepo.findByComandaId(comandaId);
        for (FacturaFurnizor f : facturi) {
            f.anuleaza();
            facturaRepo.save(f);
        }
        jurnalAudit.inregistreazaAcces("ANULARE_FACTURI_COMANDA_ANULATA", "Comanda: " + comandaId.getValue() + " (facturi anulate: " + facturi.size() + ")");
    }

    @Transactional
    public void blocheazaPlatiFurnizor(IdFurnizor furnizorId) {
        furnizoriBlocatiRepo.adauga(furnizorId);
        jurnalAudit.inregistreazaAcces("BLOCARE_PLATI_FURNIZOR", "Furnizor: " + furnizorId);
    }

    @Override
    @Transactional
    public void inregistreazaPlataFactura(UUID facturaId, Suma suma) {
        FacturaFurnizor f = facturaRepo.findById(facturaId)
            .orElseThrow(() -> new NoSuchElementException("Factura nu a fost gasita: " + facturaId));
        f.inregistreazaPlata(suma);
        facturaRepo.save(f);
        jurnalAudit.inregistreazaAcces("INREGISTREAZA_PLATA_FACTURA", "Factura: " + facturaId + " suma: " + suma);
    }
}

