package ro.uaic.feaa.sdbis.supplychain.comenzi.application.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.xml.bind.annotation.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@XmlRootElement(name = "creeazaComandaRequest")
@XmlAccessorType(XmlAccessType.FIELD)
public class CreeazaComandaRequest implements Serializable {

    @NotBlank(message = "Numarul de comanda este obligatoriu")
    @Size(min = 3, max = 100, message = "Numarul de comanda trebuie sa aiba intre 3 si 100 caractere")
    @XmlElement
    @JsonProperty("numarComanda")
    private String numarComanda;

    @NotNull(message = "Furnizor ID este obligatoriu")
    @XmlElement
    @JsonProperty("furnizorId")
    private UUID furnizorId;

    @NotNull(message = "Cumparator ID este obligatoriu")
    @XmlElement
    @JsonProperty("cumparatorId")
    private UUID cumparatorId;

    @XmlElement
    @JsonProperty("dataComanda")
    private LocalDate dataComanda;

    @NotNull(message = "Data livrare ceruta este obligatorie")
    @XmlElement
    @JsonProperty("dataLivrareCeruta")
    private LocalDate dataLivrareCeruta;

    @NotBlank(message = "Depozitul este obligatoriu")
    @Size(max = 255, message = "Depozitul nu poate depasi 255 caractere")
    @XmlElement
    @JsonProperty("depozit")
    private String depozit;

    @NotEmpty(message = "Comanda trebuie sa contina cel putin o linie")
    @Valid
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
