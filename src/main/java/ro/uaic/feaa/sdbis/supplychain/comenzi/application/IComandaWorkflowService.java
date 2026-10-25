package ro.uaic.feaa.sdbis.supplychain.comenzi.application;

import ro.uaic.feaa.sdbis.supplychain.comenzi.application.command.AnuleazaComandaCommand;
import ro.uaic.feaa.sdbis.supplychain.comenzi.application.command.CreeazaComandaCommand;
import ro.uaic.feaa.sdbis.supplychain.comenzi.application.command.InregistreazaReceptieCommand;
import ro.uaic.feaa.sdbis.supplychain.comenzi.application.dto.ComandaSummaryDTO;
import ro.uaic.feaa.sdbis.supplychain.comenzi.application.dto.PerformantaLivrariDTO;
import ro.uaic.feaa.sdbis.supplychain.comenzi.domain.ComandaAprovizionare;
import ro.uaic.feaa.sdbis.supplychain.shared.IdComanda;
import ro.uaic.feaa.sdbis.supplychain.shared.IdFurnizor;
import ro.uaic.feaa.sdbis.supplychain.shared.Perioada;

/**
 * Contract pentru Serviciul de Workflow Operațional Comenzi (Sprint 2 - ADR-003).
 * Orchestraza fluxurile de business si tranzactiile comenzilor de aprovizionare.
 */
public interface IComandaWorkflowService {

    IdComanda creeazaComanda(CreeazaComandaCommand cmd);

    void anuleazaComanda(AnuleazaComandaCommand cmd);

    void inregistreazaReceptie(InregistreazaReceptieCommand cmd);

    void valideazaSiAproba(IdComanda id);

    ComandaAprovizionare obtineComanda(IdComanda id);

    PerformantaLivrariDTO obtinePerformanta(IdFurnizor furnizorId, Perioada luna);

    ComandaSummaryDTO obtineSumarComanda(IdComanda id);
}
