package ro.uaic.feaa.sdbis.supplychain.calitate.application.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@XmlRootElement(name = "scorecardLunar")
@XmlAccessorType(XmlAccessType.FIELD)
@JsonRootName("scorecardLunar")
public class ScorecardLunarDTO implements Serializable {

    @XmlElement
    @JsonProperty("id")
    private UUID id;

    @XmlElement
    @JsonProperty("dataFinalizare")
    private LocalDate dataFinalizare;

    @XmlElement
    @JsonProperty("otif")
    private BigDecimal otif;

    @XmlElement
    @JsonProperty("rataRespingere")
    private BigDecimal rataRespingere;

    @XmlElement
    @JsonProperty("conformitatePret")
    private BigDecimal conformitatePret;

    @XmlElement
    @JsonProperty("scorTotal")
    private BigDecimal scorTotal;

    @XmlElement
    @JsonProperty("calificativ")
    private String calificativ;

    public ScorecardLunarDTO() {}

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public LocalDate getDataFinalizare() { return dataFinalizare; }
    public void setDataFinalizare(LocalDate dataFinalizare) { this.dataFinalizare = dataFinalizare; }

    public BigDecimal getOtif() { return otif; }
    public void setOtif(BigDecimal otif) { this.otif = otif; }

    public BigDecimal getRataRespingere() { return rataRespingere; }
    public void setRataRespingere(BigDecimal rataRespingere) { this.rataRespingere = rataRespingere; }

    public BigDecimal getConformitatePret() { return conformitatePret; }
    public void setConformitatePret(BigDecimal conformitatePret) { this.conformitatePret = conformitatePret; }

    public BigDecimal getScorTotal() { return scorTotal; }
    public void setScorTotal(BigDecimal scorTotal) { this.scorTotal = scorTotal; }

    public String getCalificativ() { return calificativ; }
    public void setCalificativ(String calificativ) { this.calificativ = calificativ; }
}
