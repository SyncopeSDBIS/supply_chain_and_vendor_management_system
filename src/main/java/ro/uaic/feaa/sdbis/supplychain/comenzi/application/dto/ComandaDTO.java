package ro.uaic.feaa.sdbis.supplychain.comenzi.application.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import jakarta.xml.bind.annotation.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@XmlRootElement(name = "comandaAprovizionare")
@XmlAccessorType(XmlAccessType.FIELD)
@JsonRootName("comandaAprovizionare")
public class ComandaDTO implements Serializable {

    @XmlElement
    @JsonProperty("id")
    private UUID id;

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
    @JsonProperty("dataLivrarePromisa")
    private LocalDate dataLivrarePromisa;

    @XmlElement
    @JsonProperty("depozit")
    private String depozit;

    @XmlElement
    @JsonProperty("stareComanda")
    private String stareComanda;

    @XmlElementWrapper(name = "loturi")
    @XmlElement(name = "lot")
    @JsonProperty("loturi")
    private List<LotDTO> loturi = new ArrayList<>();

    public ComandaDTO() {}

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

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

    public LocalDate getDataLivrarePromisa() { return dataLivrarePromisa; }
    public void setDataLivrarePromisa(LocalDate dataLivrarePromisa) { this.dataLivrarePromisa = dataLivrarePromisa; }

    public String getDepozit() { return depozit; }
    public void setDepozit(String depozit) { this.depozit = depozit; }

    public String getStareComanda() { return stareComanda; }
    public void setStareComanda(String stareComanda) { this.stareComanda = stareComanda; }

    public List<LotDTO> getLoturi() { return loturi; }
    public void setLoturi(List<LotDTO> loturi) { this.loturi = loturi; }
}
