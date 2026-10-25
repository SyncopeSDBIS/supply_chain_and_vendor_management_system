package ro.uaic.feaa.sdbis.supplychain.facturi.domain;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;
import ro.uaic.feaa.sdbis.supplychain.shared.CodMaterial;
import ro.uaic.feaa.sdbis.supplychain.shared.IdLot;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

@Embeddable
public class LinieNIR implements Serializable {

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "lot_id"))
    private IdLot idLot;

    @Embedded
    @AttributeOverride(name = "cod", column = @Column(name = "material_cod"))
    private CodMaterial material;

    private BigDecimal cantitateReceptionata;
    private String unitateMasura;

    protected LinieNIR() {}

    public LinieNIR(IdLot idLot, CodMaterial material, BigDecimal cantitateReceptionata, String unitateMasura) {
        this.idLot = Objects.requireNonNull(idLot);
        this.material = Objects.requireNonNull(material);
        this.cantitateReceptionata = Objects.requireNonNull(cantitateReceptionata);
        this.unitateMasura = Objects.requireNonNull(unitateMasura);
    }

    public IdLot getIdLot() { return idLot; }
    public CodMaterial getMaterial() { return material; }
    public BigDecimal getCantitateReceptionata() { return cantitateReceptionata; }
    public String getUnitateMasura() { return unitateMasura; }
}
