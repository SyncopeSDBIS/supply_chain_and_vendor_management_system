package ro.uaic.feaa.sdbis.supplychain.comenzi.application.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.UUID;

/**
 * Published Language DTO pentru metricile OTIF si conformitate pret (Figura 14).
 */
@XmlRootElement(name = "performantaLivrari")
@XmlAccessorType(XmlAccessType.FIELD)
@JsonRootName("performantaLivrari")
public class PerformantaLivrariDTO implements Serializable {

    @XmlElement
    @JsonProperty("furnizorId")
    private UUID furnizorId;

    @XmlElement
    @JsonProperty("otif")
    private BigDecimal otif;

    @XmlElement
    @JsonProperty("conformitatePret")
    private BigDecimal conformitatePret;

    public PerformantaLivrariDTO() {}

    public PerformantaLivrariDTO(UUID furnizorId, BigDecimal otif, BigDecimal conformitatePret) {
        this.furnizorId = furnizorId;
        this.otif = otif;
        this.conformitatePret = conformitatePret;
    }

    public UUID getFurnizorId() { return furnizorId; }
    public void setFurnizorId(UUID furnizorId) { this.furnizorId = furnizorId; }

    public BigDecimal getOtif() { return otif; }
    public void setOtif(BigDecimal otif) { this.otif = otif; }

    public BigDecimal getConformitatePret() { return conformitatePret; }
    public void setConformitatePret(BigDecimal conformitatePret) { this.conformitatePret = conformitatePret; }
}
