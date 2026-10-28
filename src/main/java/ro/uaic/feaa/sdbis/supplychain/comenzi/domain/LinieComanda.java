package ro.uaic.feaa.sdbis.supplychain.comenzi.domain;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;
import jakarta.validation.constraints.NotNull;
import ro.uaic.feaa.sdbis.supplychain.shared.CodMaterial;
import ro.uaic.feaa.sdbis.supplychain.shared.annotation.ValueObject;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

@ValueObject
@Embeddable
public class LinieComanda implements Serializable {

    @NotNull
    @Embedded
    @AttributeOverride(name = "cod", column = @Column(name = "material_cod"))
    private CodMaterial material;

    @NotNull
    @Embedded
    @AttributeOverride(name = "valoare", column = @Column(name = "cantitate_valoare"))
    @AttributeOverride(name = "unitateMasura", column = @Column(name = "cantitate_um"))
    private Cantitate cantitate;

    @NotNull
    @Embedded
    @AttributeOverride(name = "valoare", column = @Column(name = "pret_valoare"))
    @AttributeOverride(name = "moneda", column = @Column(name = "pret_moneda"))
    private PretUnitar pretUnitar;

    protected LinieComanda() {}

    public LinieComanda(CodMaterial material, Cantitate cantitate, PretUnitar pretUnitar) {
        this.material = Objects.requireNonNull(material, "material cannot be null");
        this.cantitate = Objects.requireNonNull(cantitate, "cantitate cannot be null");
        this.pretUnitar = Objects.requireNonNull(pretUnitar, "pretUnitar cannot be null");
    }

    public BigDecimal valoareLinie() {
        return cantitate.getValoare().multiply(pretUnitar.getValoare());
    }

    public CodMaterial getMaterial() {
        return material;
    }

    public Cantitate getCantitate() {
        return cantitate;
    }

    public PretUnitar getPretUnitar() {
        return pretUnitar;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LinieComanda that)) return false;
        return Objects.equals(material, that.material);
    }

    @Override
    public int hashCode() {
        return Objects.hash(material);
    }
}
