package ro.uaic.feaa.sdbis.supplychain.facturi.application.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.UUID;

@XmlRootElement(name = "ordinPlata")
@XmlAccessorType(XmlAccessType.FIELD)
@JsonRootName("ordinPlata")
public class OrdinPlataDTO implements Serializable {

    @XmlElement
    @JsonProperty("idOrdin")
    private UUID idOrdin;

    @XmlElement
    @JsonProperty("facturaId")
    private UUID facturaId;

    @XmlElement
    @JsonProperty("suma")
    private BigDecimal suma;

    @XmlElement
    @JsonProperty("moneda")
    private String moneda;

    @XmlElement
    @JsonProperty("creatDe")
    private UUID creatDe;

    @XmlElement
    @JsonProperty("aprobatDe")
    private UUID aprobatDe;

    @XmlElement
    @JsonProperty("stareOrdin")
    private String stareOrdin;

    public OrdinPlataDTO() {}

    public OrdinPlataDTO(UUID idOrdin, UUID facturaId, BigDecimal suma, String moneda, UUID creatDe, UUID aprobatDe, String stareOrdin) {
        this.idOrdin = idOrdin;
        this.facturaId = facturaId;
        this.suma = suma;
        this.moneda = moneda;
        this.creatDe = creatDe;
        this.aprobatDe = aprobatDe;
        this.stareOrdin = stareOrdin;
    }

    public UUID getIdOrdin() { return idOrdin; }
    public void setIdOrdin(UUID idOrdin) { this.idOrdin = idOrdin; }

    public UUID getFacturaId() { return facturaId; }
    public void setFacturaId(UUID facturaId) { this.facturaId = facturaId; }

    public BigDecimal getSuma() { return suma; }
    public void setSuma(BigDecimal suma) { this.suma = suma; }

    public String getMoneda() { return moneda; }
    public void setMoneda(String moneda) { this.moneda = moneda; }

    public UUID getCreatDe() { return creatDe; }
    public void setCreatDe(UUID creatDe) { this.creatDe = creatDe; }

    public UUID getAprobatDe() { return aprobatDe; }
    public void setAprobatDe(UUID aprobatDe) { this.aprobatDe = aprobatDe; }

    public String getStareOrdin() { return stareOrdin; }
    public void setStareOrdin(String stareOrdin) { this.stareOrdin = stareOrdin; }
}
