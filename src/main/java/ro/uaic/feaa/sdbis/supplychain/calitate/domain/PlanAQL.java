package ro.uaic.feaa.sdbis.supplychain.calitate.domain;

import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

/**
 * Value Object pentru planul de esantionare conform ISO 2859-1 (Tabelul 2.2).
 * Invariant strict: numarRespingere = numarAcceptare + 1.
 */
@Embeddable
public final class PlanAQL implements Serializable {
    private BigDecimal nivelAQL;
    private int dimensiuneEsantion;
    private int numarAcceptare;
    private int numarRespingere;

    protected PlanAQL() {}

    public PlanAQL(BigDecimal nivelAQL, int dimensiuneEsantion, int numarAcceptare, int numarRespingere) {
        if (numarRespingere != numarAcceptare + 1) {
            throw new IllegalArgumentException("Invariant ISO 2859-1 incalcat: numarRespingere (" + numarRespingere + ") trebuie sa fie numarAcceptare (" + numarAcceptare + ") + 1");
        }
        if (dimensiuneEsantion <= 0 || numarAcceptare < 0) {
            throw new IllegalArgumentException("Parametri AQL invalizi");
        }
        this.nivelAQL = Objects.requireNonNull(nivelAQL, "nivelAQL cannot be null");
        this.dimensiuneEsantion = dimensiuneEsantion;
        this.numarAcceptare = numarAcceptare;
        this.numarRespingere = numarRespingere;
    }

    public static PlanAQL standard(double aql, int esantion, int ac) {
        return new PlanAQL(BigDecimal.valueOf(aql), esantion, ac, ac + 1);
    }

    public Verdict decide(int defecteGasite) {
        if (defecteGasite <= numarAcceptare) {
            return Verdict.Conforma;
        } else {
            return Verdict.Neconforma;
        }
    }

    public BigDecimal getNivelAQL() { return nivelAQL; }
    public int getDimensiuneEsantion() { return dimensiuneEsantion; }
    public int getNumarAcceptare() { return numarAcceptare; }
    public int getNumarRespingere() { return numarRespingere; }
}
