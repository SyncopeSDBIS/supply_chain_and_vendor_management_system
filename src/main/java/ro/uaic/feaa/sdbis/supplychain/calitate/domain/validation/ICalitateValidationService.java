package ro.uaic.feaa.sdbis.supplychain.calitate.domain.validation;

import ro.uaic.feaa.sdbis.supplychain.calitate.domain.Furnizor;
import ro.uaic.feaa.sdbis.supplychain.calitate.domain.InspectieCalitate;
import ro.uaic.feaa.sdbis.supplychain.calitate.domain.PlanAQL;
import ro.uaic.feaa.sdbis.supplychain.shared.Perioada;

/**
 * Contract pentru Serviciul de Validare a Domeniului Calitate & Furnizori (ADR-003).
 * Trateaza reguli de business pentru inspectii AQL, conformitate furnizori si alocari inspectori.
 */
public interface ICalitateValidationService {

    /**
     * Valideaza planul de esantionare AQL raportat la marimea lotului receptionat.
     */
    void valideazaPlanAQL(PlanAQL plan, int marimeLot);

    /**
     * Valideaza programarea unei inspectii in raport cu certificarea/disponibilitatea inspectorului.
     */
    void valideazaInspectieProgramata(InspectieCalitate inspectie, Perioada valabilitateCertificareInspector);

    /**
     * Valideaza profilul si cerintele minime de conformitate ale unui furnizor.
     */
    void valideazaFurnizor(Furnizor furnizor);
}
