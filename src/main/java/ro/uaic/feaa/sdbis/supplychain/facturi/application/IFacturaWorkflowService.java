package ro.uaic.feaa.sdbis.supplychain.facturi.application;

import ro.uaic.feaa.sdbis.supplychain.facturi.application.command.*;
import ro.uaic.feaa.sdbis.supplychain.facturi.domain.Suma;

import ro.uaic.feaa.sdbis.supplychain.shared.annotation.WorkflowService;

import java.util.UUID;

/**
 * Contract pentru Serviciul de Workflow Operațional Facturare & Plati (Sprint 2 - ADR-003).
 */
@WorkflowService
public interface IFacturaWorkflowService {

    UUID inregistreazaFactura(InregistreazaFacturaCommand cmd);

    void contestaFactura(UUID facturaId, String motiv);

    void contestaFactura(ContestaFacturaCommand cmd);

    void genereazaNIR(GenereazaNIRCommand cmd);

    void contabilizeazaNIR(UUID nirId);

    void contabilizeazaNIR(ContabilizeazaNIRCommand cmd);

    void storneazaNIR(UUID nirId, String motiv);

    void storneazaNIR(StorneazaNIRCommand cmd);

    void reconcilieazaFactura(ReconciliereCommand cmd);

    UUID creeazaOrdinPlata(CreeazaOrdinPlataCommand cmd);

    void aprobaOrdin(UUID ordinId, UUID aprobatorId);

    void aprobaOrdin(AprobaOrdinCommand cmd);

    void transmiteOrdin(TransmiteOrdinCommand cmd);

    void inregistreazaExecutiePlata(UUID ordinId);

    void inregistreazaExecutiePlata(InregistreazaExecutiePlataCommand cmd);

    void respingeDeBanca(RespingereBancaCommand cmd);

    void inregistreazaPlataFactura(UUID facturaId, Suma suma);
}
