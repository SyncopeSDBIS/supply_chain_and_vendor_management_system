package ro.uaic.feaa.sdbis.supplychain.comenzi.application;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ro.uaic.feaa.sdbis.supplychain.comenzi.application.command.*;
import ro.uaic.feaa.sdbis.supplychain.comenzi.application.dto.ComandaSummaryDTO;
import ro.uaic.feaa.sdbis.supplychain.comenzi.application.dto.LinieSumarDTO;
import ro.uaic.feaa.sdbis.supplychain.comenzi.application.dto.PerformantaLivrariDTO;
import ro.uaic.feaa.sdbis.supplychain.comenzi.domain.*;
import ro.uaic.feaa.sdbis.supplychain.comenzi.domain.event.ComandaAnulataEvent;
import ro.uaic.feaa.sdbis.supplychain.comenzi.domain.event.ComandaReceptionataEvent;
import ro.uaic.feaa.sdbis.supplychain.shared.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class ComandaApplicationService {

    private final ComandaRepo comandaRepo;
    private final LotRepo lotRepo;
    private final IstoricLivrariRepo istoricRepo;
    private final OutboxRepo outboxRepo;
    private final InboxRepo inboxRepo;
    private final IGestiuneFurnizoriAdapter furnizoriAdapter;
    private final EstimareLeadTimeService estimareService;
    private final IJurnalAudit jurnalAudit;

    public ComandaApplicationService(ComandaRepo comandaRepo,
                                     LotRepo lotRepo,
                                     IstoricLivrariRepo istoricRepo,
                                     OutboxRepo outboxRepo,
                                     InboxRepo inboxRepo,
                                     IGestiuneFurnizoriAdapter furnizoriAdapter,
                                     EstimareLeadTimeService estimareService,
                                     IJurnalAudit jurnalAudit) {
        this.comandaRepo = comandaRepo;
        this.lotRepo = lotRepo;
        this.istoricRepo = istoricRepo;
        this.outboxRepo = outboxRepo;
        this.inboxRepo = inboxRepo;
        this.furnizoriAdapter = furnizoriAdapter;
        this.estimareService = estimareService;
        this.jurnalAudit = jurnalAudit;
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
        jurnalAudit.inregistreazaAcces("CONFIRMA_COMANDA", "Comanda: " + comanda.getNumarComanda());
    }

    public void refuzaComanda(IdComanda id) {
        ComandaAprovizionare comanda = gasesteComanda(id);
        comanda.refuza();
        comandaRepo.save(comanda);
        jurnalAudit.inregistreazaAcces("REFUZA_COMANDA", "Comanda: " + comanda.getNumarComanda());
    }

    public void inregistreazaExpediere(InregistreazaExpediereCommand cmd) {
        ComandaAprovizionare comanda = gasesteComanda(cmd.comandaId());
        comanda.inregistreazaExpediere(cmd.transportator(), cmd.awb(), cmd.loturi());
        comandaRepo.save(comanda);
        jurnalAudit.inregistreazaAcces("INREGISTREAZA_EXPEDIERE", "Comanda: " + comanda.getNumarComanda() + " AWB: " + cmd.awb());
    }

    public void inregistreazaReceptie(InregistreazaReceptieCommand cmd) {
        ComandaAprovizionare comanda = gasesteComanda(cmd.comandaId());
        ComandaReceptionataEvent event = comanda.inregistreazaReceptie(cmd.lotId(), cmd.primit());
        comandaRepo.save(comanda);
        outboxRepo.saveEvent(event);
        jurnalAudit.inregistreazaAcces("RECEPTIE_MARFA", "Comanda: " + comanda.getNumarComanda() + " Lot: " + cmd.lotId().getValue());
    }

    public void anuleazaComanda(AnuleazaComandaCommand cmd) {
        ComandaAprovizionare comanda = gasesteComanda(cmd.comandaId());
        ComandaAnulataEvent event = comanda.anuleazaComanda();
        comandaRepo.save(comanda);
        outboxRepo.saveEvent(event);
        jurnalAudit.inregistreazaAcces("ANULEAZA_COMANDA", "Comanda: " + comanda.getNumarComanda());
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
        jurnalAudit.inregistreazaAcces("CARANTINA_LOT", "Comanda: " + comanda.getNumarComanda() + " Lot: " + lotId.getValue());
    }

    public void aplicaVerdictInspectie(IdComanda comandaId, IdLot lotId, RezultatCalitateLot rezultat) {
        ComandaAprovizionare comanda = gasesteComanda(comandaId);
        comanda.aplicaVerdictInspectie(lotId, rezultat);
        comandaRepo.save(comanda);
        jurnalAudit.inregistreazaAcces("APLICA_VERDICT_INSPECTIE", "Comanda: " + comanda.getNumarComanda() + " Lot: " + lotId.getValue() + " Rezultat: " + rezultat);
    }

    public void returneazaLotFurnizorului(IdComanda comandaId, IdLot lotId) {
        ComandaAprovizionare comanda = gasesteComanda(comandaId);
        comanda.returneazaLotFurnizorului(lotId);
        comandaRepo.save(comanda);
        jurnalAudit.inregistreazaAcces("RETUR_LOT", "Comanda: " + comanda.getNumarComanda() + " Lot: " + lotId.getValue());
    }

    public ComandaSummaryDTO obtineSumarComanda(IdComanda id) {
        ComandaAprovizionare comanda = gasesteComanda(id);
        List<LinieSumarDTO> linii = comanda.getLinii().stream()
            .map(l -> new LinieSumarDTO(l.getMaterial().getCod(), l.getCantitate().getValoare(), l.getPretUnitar().getValoare()))
            .collect(Collectors.toList());
        return new ComandaSummaryDTO(comanda.getId().getValue(), linii);
    }

    public PerformantaLivrariDTO obtinePerformanta(IdFurnizor furnizorId, Perioada luna) {
        return new PerformantaLivrariDTO(furnizorId.getValue(), BigDecimal.valueOf(95.0), BigDecimal.valueOf(99.0));
    }

    private ComandaAprovizionare gasesteComanda(IdComanda id) {
        return comandaRepo.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Comanda nu a fost gasita: " + id.getValue()));
    }
}
