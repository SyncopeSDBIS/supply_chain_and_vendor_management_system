package ro.uaic.feaa.sdbis.supplychain.calitate.presentation;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import java.io.Serializable;

/**
 * Request DTO for calculating monthly supplier scorecard.
 */
public class CalculScorecardRequest implements Serializable {

    @Min(value = 2020, message = "Anul trebuie sa fie cel putin 2020")
    @Max(value = 2050, message = "Anul nu poate depasi 2050")
    private int an;

    @Min(value = 1, message = "Luna trebuie sa fie intre 1 si 12")
    @Max(value = 12, message = "Luna trebuie sa fie intre 1 si 12")
    private int luna;

    @DecimalMin(value = "0.0", message = "Rata de respingere nu poate fi negativa")
    @DecimalMax(value = "100.0", message = "Rata de respingere nu poate depasi 100%")
    private double rataRespingere = 2.0;

    public CalculScorecardRequest() {}

    public CalculScorecardRequest(int an, int luna, double rataRespingere) {
        this.an = an;
        this.luna = luna;
        this.rataRespingere = rataRespingere;
    }

    public int getAn() { return an; }
    public void setAn(int an) { this.an = an; }

    public int getLuna() { return luna; }
    public void setLuna(int luna) { this.luna = luna; }

    public double getRataRespingere() { return rataRespingere; }
    public void setRataRespingere(double rataRespingere) { this.rataRespingere = rataRespingere; }
}

