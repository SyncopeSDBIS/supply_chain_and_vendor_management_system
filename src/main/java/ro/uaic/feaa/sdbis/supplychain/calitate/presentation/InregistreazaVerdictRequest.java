package ro.uaic.feaa.sdbis.supplychain.calitate.presentation;

import jakarta.validation.constraints.PositiveOrZero;
import java.io.Serializable;

/**
 * Request DTO for recording inspection verdicts.
 */
public class InregistreazaVerdictRequest implements Serializable {

    @PositiveOrZero(message = "Numarul defectelor gasite trebuie sa fie mai mare sau egal cu zero")
    private int defecteGasite;

    public InregistreazaVerdictRequest() {}

    public InregistreazaVerdictRequest(int defecteGasite) {
        this.defecteGasite = defecteGasite;
    }

    public int getDefecteGasite() { return defecteGasite; }
    public void setDefecteGasite(int defecteGasite) { this.defecteGasite = defecteGasite; }
}

