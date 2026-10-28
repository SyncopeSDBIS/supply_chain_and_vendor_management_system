package ro.uaic.feaa.sdbis.supplychain.calitate.application;

import ro.uaic.feaa.sdbis.supplychain.calitate.application.command.AlocaInspectorCommand;
import ro.uaic.feaa.sdbis.supplychain.calitate.application.command.CalculScorecardCommand;
import ro.uaic.feaa.sdbis.supplychain.calitate.application.command.IncepeInspectieCommand;
import ro.uaic.feaa.sdbis.supplychain.calitate.application.command.InregistreazaVerdictCommand;
import ro.uaic.feaa.sdbis.supplychain.calitate.domain.*;
import ro.uaic.feaa.sdbis.supplychain.shared.*;

import ro.uaic.feaa.sdbis.supplychain.shared.annotation.WorkflowService;

import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

/**
 * Contract pentru Serviciul de Workflow Operațional Calitate & Inspectii (Sprint 2 - ADR-003).
 */
@WorkflowService
public interface ICalitateWorkflowService {

    IdFurnizor inregistreazaFurnizor(String codFiscal, String denumire, Adresa adresa,
                                     Set<Email> emailuri, Iban iban, Set<String> categorii,
                                     ConditiiContractuale conditii);

    void actualizeazaProfilFurnizor(IdFurnizor id, String denumire, Adresa adresa,
                                    Set<Email> emailuri, Iban iban, Set<String> categorii,
                                    ConditiiContractuale conditii);

    void calificaFurnizor(IdFurnizor id, String decizie, String motiv);

    UUID creeazaInspector(String firstName, String lastName);

    void adaugaCertificareInspector(UUID inspectorId, String denumire, LocalDate expirare);

    void declaraDisponibilitateInspector(UUID inspectorId, LocalDate start, LocalDate stop);

    UUID programeazaInspectie(IdLot lotId, IdComanda comandaId, IdFurnizor furnizorId,
                              CodMaterial material, PlanAQL plan, Perioada p);

    void alocaInspector(AlocaInspectorCommand cmd);

    void incepeInspectie(IncepeInspectieCommand cmd);

    void inregistreazaVerdict(InregistreazaVerdictCommand cmd);

    void solicitaReinspectie(UUID inspectieId);

    void analizeazaCauzaNCR(UUID ncrId, String cauza);

    void initiazaActiuneNCR(UUID ncrId, String descriere, String responsabil, LocalDate termen);

    void verificaNCR(UUID ncrId);

    void inchideNCR(UUID ncrId);

    void escaladeazaNCR(UUID ncrId);

    void calculeazaScorecardLunar(CalculScorecardCommand cmd);

    boolean esteFurnizorEligibil(IdFurnizor id);
}
