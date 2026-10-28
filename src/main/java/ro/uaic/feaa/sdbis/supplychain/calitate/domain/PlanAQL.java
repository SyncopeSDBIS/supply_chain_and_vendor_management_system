package ro.uaic.feaa.sdbis.supplychain.calitate.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import ro.uaic.feaa.sdbis.supplychain.shared.annotation.ValueObject;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

/**
 * Value Object pentru planul de esantionare conform ISO 2859-1 (Tabelul 2.2).
 * Invariant strict: numarRespingere = numarAcceptare + 1.
 */
@ValueObject
@Embeddable
public final class PlanAQL implements Serializable {

    @NotNull
    @DecimalMin("0.01")
    @Column(name = "aql_nivel", nullable = false)
    private BigDecimal nivelAQL;

    @Min(1)
    @Column(name = "aql_dimensiune_esantion", nullable = false)
    private int dimensiuneEsantion;

    @Min(0)
    @Column(name = "aql_numar_acceptare", nullable = false)
    private int numarAcceptare;

    @Min(1)
    @Column(name = "aql_numar_respingere", nullable = false)
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PlanAQL planAQL)) return false;
        return dimensiuneEsantion == planAQL.dimensiuneEsantion &&
               numarAcceptare == planAQL.numarAcceptare &&
               numarRespingere == planAQL.numarRespingere &&
               Objects.equals(nivelAQL, planAQL.nivelAQL);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nivelAQL, dimensiuneEsantion, numarAcceptare, numarRespingere);
    }

    @Override
    public String toString() {
        return "PlanAQL[nivel=" + nivelAQL + ", esantion=" + dimensiuneEsantion + ", Ac=" + numarAcceptare + ", Re=" + numarRespingere + "]";
    }
}
