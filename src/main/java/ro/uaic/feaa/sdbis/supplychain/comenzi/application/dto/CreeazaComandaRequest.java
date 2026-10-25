package ro.uaic.feaa.sdbis.supplychain.comenzi.application.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.xml.bind.annotation.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@XmlRootElement(name = "creeazaComandaRequest")
@XmlAccessorType(XmlAccessType.FIELD)
public class CreeazaComandaRequest implements Serializable {

    @XmlElement
    @JsonProperty("numarComanda")
    private String numarComanda;

    @XmlElement
    @JsonProperty("furnizorId")
    private UUID furnizorId;

    @XmlElement
    @JsonProperty("cumparatorId")
    private UUID cumparatorId;

    @XmlElement
    @JsonProperty("dataComanda")
    private LocalDate dataComanda;

    @XmlElement
    @JsonProperty("dataLivrareCeruta")
    private LocalDate dataLivrareCeruta;

    @XmlElement
    @JsonProperty("depozit")
    private String depozit;

    @XmlElementWrapper(name = "linii")
    @XmlElement(name = "linie")
    @JsonProperty("linii")
    private List<LinieComandaDTO> linii = new ArrayList<>();

    public CreeazaComandaRequest() {}

    public String getNumarComanda() { return numarComanda; }
    public void setNumarComanda(String numarComanda) { this.numarComanda = numarComanda; }

    public UUID getFurnizorId() { return furnizorId; }
    public void setFurnizorId(UUID furnizorId) { this.furnizorId = furnizorId; }

    public UUID getCumparatorId() { return cumparatorId; }
    public void setCumparatorId(UUID cumparatorId) { this.cumparatorId = cumparatorId; }

    public LocalDate getDataComanda() { return dataComanda; }
    public void setDataComanda(LocalDate dataComanda) { this.dataComanda = dataComanda; }

    public LocalDate getDataLivrareCeruta() { return dataLivrareCeruta; }
    public void setDataLivrareCeruta(LocalDate dataLivrareCeruta) { this.dataLivrareCeruta = dataLivrareCeruta; }

    public String getDepozit() { return depozit; }
    public void setDepozit(String depozit) { this.depozit = depozit; }

    public List<LinieComandaDTO> getLinii() { return linii; }
    public void setLinii(List<LinieComandaDTO> linii) { this.linii = linii; }
}
