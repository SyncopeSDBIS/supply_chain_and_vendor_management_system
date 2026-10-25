package ro.uaic.feaa.sdbis.supplychain.comenzi.application.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@XmlRootElement(name = "lot")
@XmlAccessorType(XmlAccessType.FIELD)
public class LotDTO implements Serializable {

    @XmlElement
    @JsonProperty("idLot")
    private UUID idLot;

    @XmlElement
    @JsonProperty("codLot")
    private String codLot;

    @XmlElement
    @JsonProperty("material")
    private String material;

    @XmlElement
    @JsonProperty("cantitate")
    private BigDecimal cantitate;

    @XmlElement
    @JsonProperty("cantitatePrimita")
    private BigDecimal cantitatePrimita;

    @XmlElement
    @JsonProperty("unitateMasura")
    private String unitateMasura;

    @XmlElement
    @JsonProperty("dataProductie")
    private LocalDate dataProductie;

    @XmlElement
    @JsonProperty("dataExpirare")
    private LocalDate dataExpirare;

    @XmlElement
    @JsonProperty("stareLot")
    private String stareLot;

    public LotDTO() {}

    public UUID getIdLot() { return idLot; }
    public void setIdLot(UUID idLot) { this.idLot = idLot; }

    public String getCodLot() { return codLot; }
    public void setCodLot(String codLot) { this.codLot = codLot; }

    public String getMaterial() { return material; }
    public void setMaterial(String material) { this.material = material; }

    public BigDecimal getCantitate() { return cantitate; }
    public void setCantitate(BigDecimal cantitate) { this.cantitate = cantitate; }

    public BigDecimal getCantitatePrimita() { return cantitatePrimita; }
    public void setCantitatePrimita(BigDecimal cantitatePrimita) { this.cantitatePrimita = cantitatePrimita; }

    public String getUnitateMasura() { return unitateMasura; }
    public void setUnitateMasura(String unitateMasura) { this.unitateMasura = unitateMasura; }

    public LocalDate getDataProductie() { return dataProductie; }
    public void setDataProductie(LocalDate dataProductie) { this.dataProductie = dataProductie; }

    public LocalDate getDataExpirare() { return dataExpirare; }
    public void setDataExpirare(LocalDate dataExpirare) { this.dataExpirare = dataExpirare; }

    public String getStareLot() { return stareLot; }
    public void setStareLot(String stareLot) { this.stareLot = stareLot; }
}
