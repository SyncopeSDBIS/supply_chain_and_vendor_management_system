package ro.uaic.feaa.sdbis.supplychain.comenzi.domain;

import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Currency;
import java.util.Objects;

@Embeddable
public final class PretUnitar implements Serializable {
    private BigDecimal valoare;
    private Currency moneda;

    protected PretUnitar() {}

    public PretUnitar(BigDecimal valoare, Currency moneda) {
        if (valoare == null || valoare.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("PretUnitar cannot be null or negative");
        }
        this.valoare = valoare;
        this.moneda = Objects.requireNonNull(moneda, "Moneda cannot be null");
    }

    public static PretUnitar of(BigDecimal valoare, String currencyCode) {
        return new PretUnitar(valoare, Currency.getInstance(currencyCode));
    }

    public static PretUnitar of(double valoare, String currencyCode) {
        return new PretUnitar(BigDecimal.valueOf(valoare), Currency.getInstance(currencyCode));
    }

    public BigDecimal getValoare() {
        return valoare;
    }

    public Currency getMoneda() {
        return moneda;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PretUnitar that)) return false;
        return Objects.equals(valoare, that.valoare) && Objects.equals(moneda, that.moneda);
    }

    @Override
    public int hashCode() {
        return Objects.hash(valoare, moneda);
    }

    @Override
    public String toString() {
        return valoare + " " + (moneda != null ? moneda.getCurrencyCode() : "");
    }
}
