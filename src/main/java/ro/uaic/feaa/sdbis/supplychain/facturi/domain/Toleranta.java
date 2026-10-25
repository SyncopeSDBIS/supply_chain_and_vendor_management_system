package ro.uaic.feaa.sdbis.supplychain.facturi.domain;

import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

/**
 * Value Object pentru verificarea incadrarii in toleranta de reconciliere (+- 2%).
 */
@Embeddable
public final class Toleranta implements Serializable {
    private BigDecimal procent; // default 2.0 (%)

    public Toleranta() {
        this.procent = BigDecimal.valueOf(2.0);
    }

    public Toleranta(BigDecimal procent) {
        this.procent = Objects.requireNonNull(procent, "procent cannot be null");
    }

    public static Toleranta standard() {
        return new Toleranta(BigDecimal.valueOf(2.0));
    }

    public boolean acopera(BigDecimal referinta, BigDecimal valoare) {
        if (referinta == null || valoare == null) return false;
        if (referinta.compareTo(BigDecimal.ZERO) == 0) {
            return valoare.compareTo(BigDecimal.ZERO) == 0;
        }

        BigDecimal diferentaAbs = referinta.subtract(valoare).abs();
        BigDecimal diffProcent = diferentaAbs.divide(referinta, 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100.0));
        return diffProcent.compareTo(procent) <= 0;
    }

    public BigDecimal getProcent() { return procent; }
}
