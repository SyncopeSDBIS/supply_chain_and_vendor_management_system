package ro.uaic.feaa.sdbis.supplychain.facturi.domain;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;
import ro.uaic.feaa.sdbis.supplychain.shared.CodMaterial;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

@Embeddable
public class LinieFactura implements Serializable {

    @Embedded
    @AttributeOverride(name = "cod", column = @Column(name = "material_cod"))
    private CodMaterial material;

    private BigDecimal cantitate;
    private String unitateMasura;

    @Embedded
    @AttributeOverride(name = "valoare", column = @Column(name = "pret_valoare"))
    @AttributeOverride(name = "moneda", column = @Column(name = "pret_moneda"))
    private Suma pretUnitar;

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
