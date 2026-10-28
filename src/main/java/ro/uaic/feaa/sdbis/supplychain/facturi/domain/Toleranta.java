package ro.uaic.feaa.sdbis.supplychain.facturi.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import ro.uaic.feaa.sdbis.supplychain.shared.annotation.ValueObject;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

/**
 * Value Object pentru verificarea incadrarii in toleranta de reconciliere (+-
 * procent).
 */
@ValueObject
@Embeddable
public final class Toleranta implements Serializable {

    private static final BigDecimal SUTA = BigDecimal.valueOf(100);
    private static final BigDecimal DEFAULT_PROCENT = BigDecimal.valueOf(2.0);

    @NotNull
    @DecimalMin("0.00")
    @Column(name = "procent_toleranta", nullable = false, precision = 5, scale = 2)
    private BigDecimal procent;

    // Modificat in protected pentru a fi accesibil doar pentru JPA/Hibernate
    protected Toleranta() {
        this.procent = DEFAULT_PROCENT;
    }

    public Toleranta(BigDecimal procent) {
        if (procent == null || procent.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Procentul de toleranta nu poate fi nul sau negativ");
        }
        this.procent = procent;
    }

    public static Toleranta standard() {
        return new Toleranta(DEFAULT_PROCENT);
    }

    public boolean acopera(BigDecimal referinta, BigDecimal valoare) {
        if (referinta == null || valoare == null)
            return false;
        if (referinta.compareTo(BigDecimal.ZERO) == 0) {
            return valoare.compareTo(BigDecimal.ZERO) == 0;
        }

        BigDecimal diferentaAbs = referinta.subtract(valoare).abs();

        // Optimizare: inmulțirea înainte de împărțire previne pierderea de precizie
        // prematură
        BigDecimal diffProcent = diferentaAbs.multiply(SUTA)
                .divide(referinta, 4, RoundingMode.HALF_UP);

        return diffProcent.compareTo(procent) <= 0;
    }

    public BigDecimal getProcent() {
        return procent;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof Toleranta that))
            return false;
        // Corecție critică: compareTo ignora scala (ex: 2.0 vs 2.00)
        return this.procent.compareTo(that.procent) == 0;
    }

    @Override
    public int hashCode() {
        // Obținem o valoare standardizată pentru a asigura consistența hash-ului
        // indiferent de scală
        return Objects.hash(procent.stripTrailingZeros());
    }

    @Override
    public String toString() {
        return procent.stripTrailingZeros().toPlainString() + "%";
    }
}
