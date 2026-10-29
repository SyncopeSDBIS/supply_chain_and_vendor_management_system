package ro.uaic.feaa.sdbis.supplychain.calitate.domain;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import ro.uaic.feaa.sdbis.supplychain.shared.CodMaterial;
import ro.uaic.feaa.sdbis.supplychain.shared.annotation.ValueObject;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

@ValueObject
@Embeddable
public final class PretNegociat implements Serializable {

    @NotNull
    @Embedded
    @AttributeOverride(name = "cod", column = @Column(name = "cod_material", nullable = false, length = 50))
    private CodMaterial material;

    @NotNull
    @PositiveOrZero
    @Column(name = "pret", nullable = false, precision = 18, scale = 4)
    private BigDecimal pret;

    protected PretNegociat() {}

    public PretNegociat(CodMaterial material, BigDecimal pret) {
        this.material = Objects.requireNonNull(material, "material cannot be null");
        this.pret = Objects.requireNonNull(pret, "pret cannot be null");
        if (pret.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Pret cannot be negative");
        }
    }

    public CodMaterial getMaterial() {
        return material;
    }

    public BigDecimal getPret() {
        return pret;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PretNegociat that)) return false;
        return Objects.equals(material, that.material) && Objects.equals(pret, that.pret);
    }

    @Override
    public int hashCode() {
        return Objects.hash(material, pret);
    }

    @Override
    public String toString() {
        return "PretNegociat[" + material + ": " + pret + "]";
    }
}

