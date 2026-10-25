package ro.uaic.feaa.sdbis.supplychain.facturi.domain;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import ro.uaic.feaa.sdbis.supplychain.shared.CodMaterial;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

@Embeddable
public class LinieFactura implements Serializable {

    @NotNull
    @Embedded
    @AttributeOverride(name = "cod", column = @Column(name = "material_cod"))
    private CodMaterial material;

    @NotNull
    @DecimalMin(value = "0.01", message = "Cantitatea pe linie factura trebuie sa fie pozitiva")
    private BigDecimal cantitate;

    @NotBlank
    private String unitateMasura;

    @NotNull
    @Embedded
    @AttributeOverride(name = "valoare", column = @Column(name = "pret_valoare"))
    @AttributeOverride(name = "moneda", column = @Column(name = "pret_moneda"))
    private Suma pretUnitar;

    @NotNull
    @DecimalMin(value = "0.00")
    @DecimalMax(value = "1.00")
    private BigDecimal cotaTVA; // ex 0.19 pentru 19%

    protected LinieFactura() {}

    public LinieFactura(CodMaterial material, BigDecimal cantitate, String unitateMasura, Suma pretUnitar, BigDecimal cotaTVA) {
        this.material = Objects.requireNonNull(material);
        this.cantitate = Objects.requireNonNull(cantitate);
        this.unitateMasura = Objects.requireNonNull(unitateMasura);
        this.pretUnitar = Objects.requireNonNull(pretUnitar);
        this.cotaTVA = cotaTVA != null ? cotaTVA : BigDecimal.valueOf(0.19);
    }

    public BigDecimal getValoareFaraTVA() {
        return cantitate.multiply(pretUnitar.getValoare());
    }

    public BigDecimal getValoareTVA() {
        return getValoareFaraTVA().multiply(cotaTVA);
    }

    public BigDecimal getValoareTotala() {
        return getValoareFaraTVA().add(getValoareTVA());
    }

    public CodMaterial getMaterial() { return material; }
    public BigDecimal getCantitate() { return cantitate; }
    public String getUnitateMasura() { return unitateMasura; }
    public Suma getPretUnitar() { return pretUnitar; }
    public BigDecimal getCotaTVA() { return cotaTVA; }
}
