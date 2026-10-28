package ro.uaic.feaa.sdbis.supplychain.facturi.application.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import jakarta.xml.bind.annotation.*;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@XmlRootElement(name = "nir")
@XmlAccessorType(XmlAccessType.FIELD)
@JsonRootName("nir")
public class NIRDTO implements Serializable {

    @XmlElement
    @JsonProperty("idNIR")
    private UUID idNIR;

    @XmlElement
    @JsonProperty("numarNIR")
    private String numarNIR;

    @XmlElement
    @JsonProperty("comandaId")
    private UUID comandaId;

    @XmlElement
    @JsonProperty("eventSursaId")
    private UUID eventSursaId;

    @XmlElement
    @JsonProperty("dataReceptie")
    private LocalDate dataReceptie;

    @XmlElement
    @JsonProperty("stareNIR")
    private String stareNIR;

    @XmlElementWrapper(name = "linii")
    @XmlElement(name = "linie")
    @JsonProperty("linii")
    private List<LinieNIRDTO> linii = new ArrayList<>();

    public NIRDTO() {}

    public UUID getIdNIR() { return idNIR; }
    public void setIdNIR(UUID idNIR) { this.idNIR = idNIR; }

    public String getNumarNIR() { return numarNIR; }
    public void setNumarNIR(String numarNIR) { this.numarNIR = numarNIR; }

    public UUID getComandaId() { return comandaId; }
    public void setComandaId(UUID comandaId) { this.comandaId = comandaId; }

    public UUID getEventSursaId() { return eventSursaId; }
    public void setEventSursaId(UUID eventSursaId) { this.eventSursaId = eventSursaId; }

    public LocalDate getDataReceptie() { return dataReceptie; }
    public void setDataReceptie(LocalDate dataReceptie) { this.dataReceptie = dataReceptie; }

    public String getStareNIR() { return stareNIR; }
    public void setStareNIR(String stareNIR) { this.stareNIR = stareNIR; }

    public List<LinieNIRDTO> getLinii() { return linii; }
    public void setLinii(List<LinieNIRDTO> linii) { this.linii = linii; }
}
