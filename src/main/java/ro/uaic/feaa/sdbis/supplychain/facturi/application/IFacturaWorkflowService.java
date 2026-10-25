package ro.uaic.feaa.sdbis.supplychain.facturi.application;

import ro.uaic.feaa.sdbis.supplychain.facturi.application.command.CreeazaOrdinPlataCommand;
import ro.uaic.feaa.sdbis.supplychain.facturi.application.command.GenereazaNIRCommand;
import ro.uaic.feaa.sdbis.supplychain.facturi.application.command.InregistreazaFacturaCommand;
import ro.uaic.feaa.sdbis.supplychain.facturi.application.command.ReconciliereCommand;
import ro.uaic.feaa.sdbis.supplychain.facturi.application.command.TransmiteOrdinCommand;

import java.util.UUID;

/**
 * Contract pentru Serviciul de Workflow Operațional Facturare & Plati (Sprint 2 - ADR-003).
 */
public interface IFacturaWorkflowService {

    UUID inregistreazaFactura(InregistreazaFacturaCommand cmd);

    void contestaFactura(UUID facturaId, String motiv);

    void genereazaNIR(GenereazaNIRCommand cmd);

    void contabilizeazaNIR(UUID nirId);

    void storneazaNIR(UUID nirId, String motiv);

    void reconcilieazaFactura(ReconciliereCommand cmd);

    UUID creeazaOrdinPlata(CreeazaOrdinPlataCommand cmd);

    void aprobaOrdin(UUID ordinId, UUID aprobatorId);

    void transmiteOrdin(TransmiteOrdinCommand cmd);

    void inregistreazaExecutiePlata(UUID ordinId);

    void inregistreazaPlataFactura(UUID facturaId, ro.uaic.feaa.sdbis.supplychain.facturi.domain.Suma suma);
}
