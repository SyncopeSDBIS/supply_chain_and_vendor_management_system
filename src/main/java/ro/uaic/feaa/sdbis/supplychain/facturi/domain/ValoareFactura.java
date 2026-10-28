package ro.uaic.feaa.sdbis.supplychain.facturi.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import ro.uaic.feaa.sdbis.supplychain.shared.annotation.ValueObject;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Objects;

@ValueObject
@Embeddable
public final class ValoareFactura implements Serializable {

    @NotNull
    @DecimalMin("0.00")
    @Column(name = "valoare_fara_tva", nullable = false)
    private BigDecimal valoareFaraTVA;

    @NotNull
    @DecimalMin("0.00")
    @Column(name = "valoare_tva", nullable = false)
    private BigDecimal valoareTVA;

    @NotNull
    @DecimalMin("0.00")
    @Column(name = "valoare_totala", nullable = false)
    private BigDecimal valoareTotala;

    protected ValoareFactura() {}

    public ValoareFactura(BigDecimal valoareFaraTVA, BigDecimal valoareTVA, BigDecimal valoareTotala) {
        Objects.requireNonNull(valoareFaraTVA, "valoareFaraTVA cannot be null");
        Objects.requireNonNull(valoareTVA, "valoareTVA cannot be null");
        Objects.requireNonNull(valoareTotala, "valoareTotala cannot be null");
        this.valoareFaraTVA = valoareFaraTVA.setScale(2, RoundingMode.HALF_UP);
        this.valoareTVA = valoareTVA.setScale(2, RoundingMode.HALF_UP);
        this.valoareTotala = valoareTotala.setScale(2, RoundingMode.HALF_UP);
    }

    public static ValoareFactura calculeaza(List<LinieFactura> linii) {
        BigDecimal faraTVA = BigDecimal.ZERO;
        BigDecimal tva = BigDecimal.ZERO;
        if (linii != null) {
            for (LinieFactura l : linii) {
                faraTVA = faraTVA.add(l.getValoareFaraTVA());
                tva = tva.add(l.getValoareTVA());
            }
        }
        return new ValoareFactura(faraTVA, tva, faraTVA.add(tva));
    }

    public BigDecimal getValoareFaraTVA() { return valoareFaraTVA; }
    public BigDecimal getValoareTVA() { return valoareTVA; }
    public BigDecimal getValoareTotala() { return valoareTotala; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ValoareFactura that)) return false;
        return Objects.equals(valoareFaraTVA, that.valoareFaraTVA) &&
               Objects.equals(valoareTVA, that.valoareTVA) &&
               Objects.equals(valoareTotala, that.valoareTotala);
    }

    @Override
    public int hashCode() {
        return Objects.hash(valoareFaraTVA, valoareTVA, valoareTotala);
    }

    @Override
    public String toString() {
        return "Total: " + valoareTotala + " (Net: " + valoareFaraTVA + ", TVA: " + valoareTVA + ")";
    }
}
