package ro.uaic.feaa.sdbis.supplychain.comenzi.application;

import ro.uaic.feaa.sdbis.supplychain.comenzi.application.command.AnuleazaComandaCommand;
import ro.uaic.feaa.sdbis.supplychain.comenzi.application.command.CreeazaComandaCommand;
import ro.uaic.feaa.sdbis.supplychain.comenzi.application.command.InregistreazaReceptieCommand;
import ro.uaic.feaa.sdbis.supplychain.comenzi.application.dto.ComandaSumarViewDTO;
import ro.uaic.feaa.sdbis.supplychain.comenzi.application.dto.PerformantaLivrariSumarDTO;
import ro.uaic.feaa.sdbis.supplychain.comenzi.domain.ComandaAprovizionare;
import ro.uaic.feaa.sdbis.supplychain.comenzi.domain.policy.PoliticaAprobareComanda;
import ro.uaic.feaa.sdbis.supplychain.shared.IdComanda;
import ro.uaic.feaa.sdbis.supplychain.shared.IdFurnizor;
import ro.uaic.feaa.sdbis.supplychain.shared.Perioada;
import ro.uaic.feaa.sdbis.supplychain.shared.annotation.WorkflowService;

import java.util.UUID;

/**
 * Contract pentru Serviciul de Workflow Operațional Comenzi (Sprint 2 - ADR-003).
 * Orchestraza fluxurile de business si tranzactiile comenzilor de aprovizionare.
 */
@WorkflowService
public interface IComandaWorkflowService {

    IdComanda creeazaComanda(CreeazaComandaCommand cmd);

    void anuleazaComanda(AnuleazaComandaCommand cmd);

    void inregistreazaReceptie(InregistreazaReceptieCommand cmd);

    void valideazaSiAproba(IdComanda id);

    void valideazaSiAproba(IdComanda id, UUID utilizatorId, PoliticaAprobareComanda.NivelAprobare nivelUtilizator);

    void inchideComanda(IdComanda id);

    ComandaAprovizionare obtineComanda(IdComanda id);

    PerformantaLivrariSumarDTO obtinePerformanta(IdFurnizor furnizorId, Perioada luna);

    ComandaSumarViewDTO obtineSumarComanda(IdComanda id);
}
