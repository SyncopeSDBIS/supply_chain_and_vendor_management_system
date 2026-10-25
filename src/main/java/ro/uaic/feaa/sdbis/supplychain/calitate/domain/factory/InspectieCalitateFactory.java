package ro.uaic.feaa.sdbis.supplychain.calitate.domain.factory;

import ro.uaic.feaa.sdbis.supplychain.calitate.domain.InspectieCalitate;
import ro.uaic.feaa.sdbis.supplychain.calitate.domain.PlanAQL;
import ro.uaic.feaa.sdbis.supplychain.shared.CodMaterial;
import ro.uaic.feaa.sdbis.supplychain.shared.IdComanda;
import ro.uaic.feaa.sdbis.supplychain.shared.IdFurnizor;
import ro.uaic.feaa.sdbis.supplychain.shared.IdLot;
import ro.uaic.feaa.sdbis.supplychain.shared.Perioada;
import ro.uaic.feaa.sdbis.supplychain.shared.exception.DomainValidationException;

import java.util.Objects;
import java.util.UUID;

/**
 * Aggregate Factory pentru InspectieCalitate (Sprint 2 - ADR-003).
 * Garanteaza initializarea consistenta a agregatului in starea initiala Programata.
 */
public class InspectieCalitateFactory {

    public static InspectieCalitate programeaza(IdLot lotId,
                                                IdComanda comandaId,
                                                IdFurnizor furnizorId,
                                                CodMaterial material,
                                                PlanAQL plan,
                                                Perioada perioadaPlanificata) {
        if (lotId == null) {
            throw new DomainValidationException("LOT_LIPSA", "Identificatorul lotului este obligatoriu!");
        }
        if (comandaId == null) {
            throw new DomainValidationException("COMANDA_LIPSA", "Identificatorul comenzii este obligatoriu!");
        }
        if (furnizorId == null) {
            throw new DomainValidationException("FURNIZOR_LIPSA", "Identificatorul furnizorului este obligatoriu!");
        }
        if (material == null) {
            throw new DomainValidationException("MATERIAL_LIPSA", "Materialul inspectat este obligatoriu!");
        }
        if (plan == null) {
            throw new DomainValidationException("PLAN_LIPSA", "Planul AQL este obligatoriu!");
        }
        if (plan.getDimensiuneEsantion() <= 0) {
            throw new DomainValidationException("ESANTION_INVALID", "Marimea esantionului trebuie sa fie strict pozitiva!");
        }
        if (perioadaPlanificata == null) {
            throw new DomainValidationException("PERIOADA_LIPSA", "Perioada planificata a inspectiei este obligatorie!");
        }

        UUID idInspectie = UUID.randomUUID();
        return new InspectieCalitate(idInspectie, lotId, comandaId, furnizorId, material, plan, perioadaPlanificata);
    }
}
