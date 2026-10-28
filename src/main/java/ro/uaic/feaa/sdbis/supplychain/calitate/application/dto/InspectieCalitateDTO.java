package ro.uaic.feaa.sdbis.supplychain.calitate.application.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

import java.io.Serializable;
import java.util.UUID;

@XmlRootElement(name = "inspectieCalitate")
@XmlAccessorType(XmlAccessType.FIELD)
@JsonRootName("inspectieCalitate")
public class InspectieCalitateDTO implements Serializable {

    @XmlElement
    @JsonProperty("idInspectie")
    private UUID idInspectie;

    @XmlElement
    @JsonProperty("lotId")
    private UUID lotId;

    @XmlElement
    @JsonProperty("comandaId")
    private UUID comandaId;

    @XmlElement
    @JsonProperty("furnizorId")
    private UUID furnizorId;

    @XmlElement
    @JsonProperty("materialCod")
    private String materialCod;

    @XmlElement
    @JsonProperty("inspectorId")
    private UUID inspectorId;

    @XmlElement
    @JsonProperty("stareInspectie")
    private String stareInspectie;

    @XmlElement
    @JsonProperty("verdict")
    private String verdict;

    @XmlElement
    @JsonProperty("bucatiDefecte")
    private Integer bucatiDefecte;

    public InspectieCalitateDTO() {}

    public UUID getIdInspectie() { return idInspectie; }
    public void setIdInspectie(UUID idInspectie) { this.idInspectie = idInspectie; }

    public UUID getLotId() { return lotId; }
    public void setLotId(UUID lotId) { this.lotId = lotId; }

    public UUID getComandaId() { return comandaId; }
    public void setComandaId(UUID comandaId) { this.comandaId = comandaId; }

    public UUID getFurnizorId() { return furnizorId; }
    public void setFurnizorId(UUID furnizorId) { this.furnizorId = furnizorId; }

    public String getMaterialCod() { return materialCod; }
    public void setMaterialCod(String materialCod) { this.materialCod = materialCod; }

    public UUID getInspectorId() { return inspectorId; }
    public void setInspectorId(UUID inspectorId) { this.inspectorId = inspectorId; }

    public String getStareInspectie() { return stareInspectie; }
    public void setStareInspectie(String stareInspectie) { this.stareInspectie = stareInspectie; }

    public String getVerdict() { return verdict; }
    public void setVerdict(String verdict) { this.verdict = verdict; }

    public Integer getBucatiDefecte() { return bucatiDefecte; }
    public void setBucatiDefecte(Integer bucatiDefecte) { this.bucatiDefecte = bucatiDefecte; }
}
