package ro.uaic.feaa.sdbis.supplychain.calitate.application.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

import java.io.Serializable;
import java.util.UUID;

@XmlRootElement(name = "ncr")
@XmlAccessorType(XmlAccessType.FIELD)
@JsonRootName("ncr")
public class NCRDTO implements Serializable {

    @XmlElement
    @JsonProperty("idNCR")
    private UUID idNCR;

    @XmlElement
    @JsonProperty("inspectieId")
    private UUID inspectieId;

    @XmlElement
    @JsonProperty("furnizorId")
    private UUID furnizorId;

    @XmlElement
    @JsonProperty("lotId")
    private UUID lotId;

    @XmlElement
    @JsonProperty("cauzaRadacina")
    private String cauzaRadacina;

    @XmlElement
    @JsonProperty("actiuneCorectiva")
    private String actiuneCorectiva;

    @XmlElement
    @JsonProperty("stareNCR")
    private String stareNCR;

    public NCRDTO() {}

    public UUID getIdNCR() { return idNCR; }
    public void setIdNCR(UUID idNCR) { this.idNCR = idNCR; }

    public UUID getInspectieId() { return inspectieId; }
    public void setInspectieId(UUID inspectieId) { this.inspectieId = inspectieId; }

    public UUID getFurnizorId() { return furnizorId; }
    public void setFurnizorId(UUID furnizorId) { this.furnizorId = furnizorId; }

    public UUID getLotId() { return lotId; }
    public void setLotId(UUID lotId) { this.lotId = lotId; }

    public String getCauzaRadacina() { return cauzaRadacina; }
    public void setCauzaRadacina(String cauzaRadacina) { this.cauzaRadacina = cauzaRadacina; }

    public String getActiuneCorectiva() { return actiuneCorectiva; }
    public void setActiuneCorectiva(String actiuneCorectiva) { this.actiuneCorectiva = actiuneCorectiva; }

    public String getStareNCR() { return stareNCR; }
    public void setStareNCR(String stareNCR) { this.stareNCR = stareNCR; }
}
