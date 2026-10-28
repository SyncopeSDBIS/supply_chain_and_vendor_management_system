package ro.uaic.feaa.sdbis.supplychain.calitate.domain;

import jakarta.persistence.Embeddable;
import ro.uaic.feaa.sdbis.supplychain.shared.annotation.ValueObject;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

/**
 * Value Object pentru scorul lunar ponderat conform cerintelor OOAD:
 * 40% OTIF + 40% Calitate (100 - rata respingere) + 20% Pret.
 */
@ValueObject
@Embeddable
public final class Scor implements Serializable {
    private BigDecimal valoare;

    protected Scor() {}

    public Scor(BigDecimal valoare) {
        if (valoare == null || valoare.compareTo(BigDecimal.ZERO) < 0 || valoare.compareTo(BigDecimal.valueOf(100.0)) > 0) {
            throw new IllegalArgumentException("Scorul trebuie sa fie intre 0 si 100. Valoare: " + valoare);
        }
        this.valoare = valoare.setScale(2, RoundingMode.HALF_UP);
    }

    public static Scor ponderat(BigDecimal otif, BigDecimal calitate, BigDecimal pret) {
        Objects.requireNonNull(otif, "otif cannot be null");
        Objects.requireNonNull(calitate, "calitate cannot be null");
        Objects.requireNonNull(pret, "pret cannot be null");

        // 40% OTIF + 40% Calitate + 20% Pret
        BigDecimal v = otif.multiply(BigDecimal.valueOf(0.40))
            .add(calitate.multiply(BigDecimal.valueOf(0.40)))
            .add(pret.multiply(BigDecimal.valueOf(0.20)));
        return new Scor(v);
    }

    public Calificativ calificativ() {
        if (valoare.compareTo(BigDecimal.valueOf(90.0)) >= 0) return Calificativ.A;
        if (valoare.compareTo(BigDecimal.valueOf(80.0)) >= 0) return Calificativ.B;
        if (valoare.compareTo(BigDecimal.valueOf(60.0)) >= 0) return Calificativ.C;
        return Calificativ.D;
    }

    public BigDecimal getValoare() {
        return valoare;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Scor scor)) return false;
        return Objects.equals(valoare, scor.valoare);
    }

    @Override
    public int hashCode() {
        return Objects.hash(valoare);
    }

    @Override
    public String toString() {
        return valoare + " (" + calificativ() + ")";
    }
}
