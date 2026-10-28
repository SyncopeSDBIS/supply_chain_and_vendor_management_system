package ro.uaic.feaa.sdbis.supplychain.comenzi.domain;

import jakarta.persistence.Embeddable;
import ro.uaic.feaa.sdbis.supplychain.shared.annotation.ValueObject;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

@ValueObject
@Embeddable
public final class Cantitate implements Serializable {
    private BigDecimal valoare;
    private String unitateMasura;

    protected Cantitate() {}

    public Cantitate(BigDecimal valoare, String unitateMasura) {
        if (valoare == null || valoare.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Cantitate valoare cannot be null or negative");
        }
        if (unitateMasura == null || unitateMasura.trim().isEmpty()) {
            throw new IllegalArgumentException("UnitateMasura cannot be null or empty");
        }
        this.valoare = valoare;
        this.unitateMasura = unitateMasura.trim().toUpperCase();
    }

    public static Cantitate of(double valoare, String unitate) {
        return new Cantitate(BigDecimal.valueOf(valoare), unitate);
    }

    public static Cantitate of(BigDecimal valoare, String unitate) {
        return new Cantitate(valoare, unitate);
    }

    public static Cantitate zero(String unitate) {
        return new Cantitate(BigDecimal.ZERO, unitate);
    }

    public Cantitate adauga(Cantitate alta) {
        Objects.requireNonNull(alta, "alta cantitate cannot be null");
        if (!this.unitateMasura.equalsIgnoreCase(alta.unitateMasura)) {
            throw new IllegalArgumentException("Cannot add different units: " + this.unitateMasura + " vs " + alta.unitateMasura);
        }
        return new Cantitate(this.valoare.add(alta.valoare), this.unitateMasura);
    }

    public boolean acopera(Cantitate ceruta) {
        Objects.requireNonNull(ceruta, "ceruta cantitate cannot be null");
        if (!this.unitateMasura.equalsIgnoreCase(ceruta.unitateMasura)) {
            throw new IllegalArgumentException("Cannot compare different units: " + this.unitateMasura + " vs " + ceruta.unitateMasura);
        }
        return this.valoare.compareTo(ceruta.valoare) >= 0;
    }

    public boolean esteAcoperitDe(Cantitate acoperitor) {
        return acopera(acoperitor);
    }

    public BigDecimal getValoare() {
        return valoare;
    }

    public String getUnitateMasura() {
        return unitateMasura;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Cantitate that)) return false;
        return Objects.equals(valoare, that.valoare) && Objects.equals(unitateMasura, that.unitateMasura);
    }

    @Override
    public int hashCode() {
        return Objects.hash(valoare, unitateMasura);
    }

    @Override
    public String toString() {
        return valoare + " " + unitateMasura;
    }
}
