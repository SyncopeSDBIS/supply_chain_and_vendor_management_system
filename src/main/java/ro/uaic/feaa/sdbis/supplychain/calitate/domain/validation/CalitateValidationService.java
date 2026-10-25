package ro.uaic.feaa.sdbis.supplychain.calitate.domain.validation;

import ro.uaic.feaa.sdbis.supplychain.calitate.domain.Furnizor;
import ro.uaic.feaa.sdbis.supplychain.calitate.domain.InspectieCalitate;
import ro.uaic.feaa.sdbis.supplychain.calitate.domain.PlanAQL;
import ro.uaic.feaa.sdbis.supplychain.shared.Perioada;
import ro.uaic.feaa.sdbis.supplychain.shared.exception.DomainValidationException;

import java.util.Objects;

/**
 * Implementare Domain Validation Service pentru Calitate (Sprint 2 - ADR-003).
 * Respecta izolarea pura de domeniu (ADR-011).
 */
public class CalitateValidationService implements ICalitateValidationService {

    @Override
    public void valideazaPlanAQL(PlanAQL plan, int marimeLot) {
        Objects.requireNonNull(plan, "Planul AQL nu poate fi null!");

        if (marimeLot <= 0) {
            throw new DomainValidationException("LOT_INVALID", "Marimea lotului supus inspectiei trebuie sa fie strict pozitiva!");
        }

        if (plan.getDimensiuneEsantion() <= 0) {
            throw new DomainValidationException("ESANTION_INVALID", "Marimea esantionului de control trebuie sa fie strict pozitiva!");
        }

        if (plan.getDimensiuneEsantion() > marimeLot) {
            throw new DomainValidationException("ESANTION_DEPASIT",
                "Marimea esantionului (" + plan.getDimensiuneEsantion() + ") nu poate depasi marimea totala a lotului (" + marimeLot + ")!");
        }

        if (plan.getNivelAQL() == null) {
            throw new DomainValidationException("NIVEL_AQL_LIPSA", "Nivelul de severitate AQL este obligatoriu!");
        }
    }

    @Override
    public void valideazaInspectieProgramata(InspectieCalitate inspectie, Perioada valabilitateCertificareInspector) {
        Objects.requireNonNull(inspectie, "Inspectia nu poate fi null!");
        Objects.requireNonNull(valabilitateCertificareInspector, "Perioada de valabilitate a certificarii inspectorului este obligatorie!");

        Perioada perioadaInspectie = inspectie.getPerioadaPlanificata();
        if (perioadaInspectie == null) {
            throw new DomainValidationException("PERIOADA_INSPECTIE_LIPSA", "Perioada planificata a inspectiei nu este setata!");
        }

        if (!perioadaInspectie.esteInclusaIn(valabilitateCertificareInspector)) {
            throw new DomainValidationException("CERTIFICARE_INSPECTOR_EXPIRATA",
                "Certificarea inspectorului nu este valida pe durata programata a inspectiei!");
        }
    }

    @Override
    public void valideazaFurnizor(Furnizor furnizor) {
        Objects.requireNonNull(furnizor, "Furnizorul nu poate fi null!");

        if (furnizor.getCodFiscal() == null || furnizor.getCodFiscal().trim().isEmpty()) {
            throw new DomainValidationException("COD_FISCAL_LIPSA", "Codul fiscal al furnizorului este obligatoriu!");
        }

        if (furnizor.getDenumire() == null || furnizor.getDenumire().trim().isEmpty()) {
            throw new DomainValidationException("DENUMIRE_LIPSA", "Denumirea comerciala a furnizorului este obligatorie!");
        }

        if (furnizor.getIban() == null || furnizor.getIban().getValoare() == null || furnizor.getIban().getValoare().trim().isEmpty()) {
            throw new DomainValidationException("IBAN_INVALID", "Contul IBAN este obligatoriu si trebuie sa fie valid!");
        }

        if (furnizor.getCategoriiMateriale() == null || furnizor.getCategoriiMateriale().isEmpty()) {
            throw new DomainValidationException("CATEGORII_LIPSA", "Furnizorul trebuie sa aiba configurata cel putin o categorie de materiale!");
        }
    }
}
