package ro.uaic.feaa.sdbis.supplychain.facturi.domain;

import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Currency;
import java.util.Objects;

@Embeddable
public final class Suma implements Serializable {
    private BigDecimal valoare;
    private Currency moneda;

    protected Suma() {}

    public Suma(BigDecimal valoare, Currency moneda) {
        if (valoare == null || valoare.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Suma cannot be null or negative");
        }
        this.valoare = valoare.setScale(2, RoundingMode.HALF_UP);
        this.moneda = Objects.requireNonNull(moneda, "moneda cannot be null");
    }

    public static Suma of(BigDecimal valoare, String currencyCode) {
        return new Suma(valoare, Currency.getInstance(currencyCode));
    }

    public static Suma of(double valoare, String currencyCode) {
        return new Suma(BigDecimal.valueOf(valoare), Currency.getInstance(currencyCode));
    }

    public static Suma zero(Currency moneda) {
        return new Suma(BigDecimal.ZERO, moneda);
    }

    public Suma adauga(Suma alta) {
        Objects.requireNonNull(alta, "alta suma cannot be null");
        if (!this.moneda.equals(alta.moneda)) {
            throw new IllegalArgumentException("Cannot add different currencies: " + this.moneda + " vs " + alta.moneda);
        }
        return new Suma(this.valoare.add(alta.valoare), this.moneda);
    }

    public Suma scade(Suma alta) {
        Objects.requireNonNull(alta, "alta suma cannot be null");
        if (!this.moneda.equals(alta.moneda)) {
            throw new IllegalArgumentException("Cannot subtract different currencies: " + this.moneda + " vs " + alta.moneda);
        }
        if (alta.valoare.compareTo(this.valoare) > 0) {
            throw new IllegalArgumentException("Suma scazuta (" + alta.valoare + ") depaseste suma curenta (" + this.valoare + ")");
        }
        return new Suma(this.valoare.subtract(alta.valoare), this.moneda);
    }

    public boolean esteMaiMareDecat(Suma alta) {
        Objects.requireNonNull(alta, "alta suma cannot be null");
        if (!this.moneda.equals(alta.moneda)) {
            throw new IllegalArgumentException("Cannot compare different currencies");
        }
        return this.valoare.compareTo(alta.valoare) > 0;
    }

    public BigDecimal getValoare() { return valoare; }
    public Currency getMoneda() { return moneda; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Suma suma)) return false;
        return Objects.equals(valoare, suma.valoare) && Objects.equals(moneda, suma.moneda);
    }

    @Override
    public int hashCode() { return Objects.hash(valoare, moneda); }

    @Override
    public String toString() { return valoare + " " + (moneda != null ? moneda.getCurrencyCode() : ""); }
}
