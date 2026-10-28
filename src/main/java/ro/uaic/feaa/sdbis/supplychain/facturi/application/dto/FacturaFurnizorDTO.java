package ro.uaic.feaa.sdbis.supplychain.facturi.application.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import jakarta.xml.bind.annotation.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@XmlRootElement(name = "facturaFurnizor")
@XmlAccessorType(XmlAccessType.FIELD)
@JsonRootName("facturaFurnizor")
public class FacturaFurnizorDTO implements Serializable {

    @XmlElement
    @JsonProperty("idFactura")
    private UUID idFactura;

    @XmlElement
    @JsonProperty("serie")
    private String serie;

    @XmlElement
    @JsonProperty("numar")
    private String numar;

    @XmlElement
    @JsonProperty("dataEmitere")
    private LocalDate dataEmitere;

    @XmlElement
    @JsonProperty("dataScadenta")
    private LocalDate dataScadenta;

    @XmlElement
    @JsonProperty("comandaId")
    private UUID comandaId;

    @XmlElement
    @JsonProperty("furnizorId")
    private UUID furnizorId;

    @XmlElement
    @JsonProperty("stareFactura")
    private String stareFactura;

    @XmlElement
    @JsonProperty("valoareTotala")
    private BigDecimal valoareTotala;

    @XmlElement
    @JsonProperty("valoareFaraTva")
    private BigDecimal valoareFaraTva;

    @XmlElement
    @JsonProperty("valoareTva")
    private BigDecimal valoareTva;

    @XmlElement
    @JsonProperty("sumaPlatita")
    private BigDecimal sumaPlatita;

    @XmlElement
    @JsonProperty("restDePlata")
    private BigDecimal restDePlata;

    @XmlElement
    @JsonProperty("moneda")
    private String moneda;

    @XmlElementWrapper(name = "linii")
    @XmlElement(name = "linie")
    @JsonProperty("linii")
    private List<LinieFacturaDTO> linii = new ArrayList<>();

    public FacturaFurnizorDTO() {}

    public UUID getIdFactura() { return idFactura; }
    public void setIdFactura(UUID idFactura) { this.idFactura = idFactura; }

    public String getSerie() { return serie; }
    public void setSerie(String serie) { this.serie = serie; }

    public String getNumar() { return numar; }
    public void setNumar(String numar) { this.numar = numar; }

    public LocalDate getDataEmitere() { return dataEmitere; }
    public void setDataEmitere(LocalDate dataEmitere) { this.dataEmitere = dataEmitere; }

    public LocalDate getDataScadenta() { return dataScadenta; }
    public void setDataScadenta(LocalDate dataScadenta) { this.dataScadenta = dataScadenta; }

    public UUID getComandaId() { return comandaId; }
    public void setComandaId(UUID comandaId) { this.comandaId = comandaId; }

    public UUID getFurnizorId() { return furnizorId; }
    public void setFurnizorId(UUID furnizorId) { this.furnizorId = furnizorId; }

    public String getStareFactura() { return stareFactura; }
    public void setStareFactura(String stareFactura) { this.stareFactura = stareFactura; }

    public BigDecimal getValoareTotala() { return valoareTotala; }
    public void setValoareTotala(BigDecimal valoareTotala) { this.valoareTotala = valoareTotala; }

    public BigDecimal getValoareFaraTva() { return valoareFaraTva; }
    public void setValoareFaraTva(BigDecimal valoareFaraTva) { this.valoareFaraTva = valoareFaraTva; }

    public BigDecimal getValoareTva() { return valoareTva; }
    public void setValoareTva(BigDecimal valoareTva) { this.valoareTva = valoareTva; }

    public BigDecimal getSumaPlatita() { return sumaPlatita; }
    public void setSumaPlatita(BigDecimal sumaPlatita) { this.sumaPlatita = sumaPlatita; }

    public BigDecimal getRestDePlata() { return restDePlata; }
    public void setRestDePlata(BigDecimal restDePlata) { this.restDePlata = restDePlata; }

    public String getMoneda() { return moneda; }
    public void setMoneda(String moneda) { this.moneda = moneda; }

    public List<LinieFacturaDTO> getLinii() { return linii; }
    public void setLinii(List<LinieFacturaDTO> linii) { this.linii = linii; }
}
