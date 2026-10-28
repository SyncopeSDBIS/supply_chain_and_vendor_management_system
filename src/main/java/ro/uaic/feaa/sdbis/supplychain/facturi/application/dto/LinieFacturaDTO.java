package ro.uaic.feaa.sdbis.supplychain.facturi.application.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

import java.io.Serializable;
import java.math.BigDecimal;

@XmlRootElement(name = "linieFactura")
@XmlAccessorType(XmlAccessType.FIELD)
@JsonRootName("linieFactura")
public class LinieFacturaDTO implements Serializable {

    @XmlElement
    @JsonProperty("materialCod")
    private String materialCod;

    @XmlElement
    @JsonProperty("cantitate")
    private BigDecimal cantitate;

    @XmlElement
    @JsonProperty("unitateMasura")
    private String unitateMasura;

    @XmlElement
    @JsonProperty("pretUnitar")
    private BigDecimal pretUnitar;

    @XmlElement
    @JsonProperty("moneda")
    private String moneda;

    @XmlElement
    @JsonProperty("cotaTva")
    private BigDecimal cotaTva;

    public LinieFacturaDTO() {}

    public LinieFacturaDTO(String materialCod, BigDecimal cantitate, String unitateMasura, BigDecimal pretUnitar, String moneda, BigDecimal cotaTva) {
        this.materialCod = materialCod;
        this.cantitate = cantitate;
        this.unitateMasura = unitateMasura;
        this.pretUnitar = pretUnitar;
        this.moneda = moneda;
        this.cotaTva = cotaTva;
    }

    public String getMaterialCod() { return materialCod; }
    public void setMaterialCod(String materialCod) { this.materialCod = materialCod; }

    public BigDecimal getCantitate() { return cantitate; }
    public void setCantitate(BigDecimal cantitate) { this.cantitate = cantitate; }

    public String getUnitateMasura() { return unitateMasura; }
    public void setUnitateMasura(String unitateMasura) { this.unitateMasura = unitateMasura; }

    public BigDecimal getPretUnitar() { return pretUnitar; }
    public void setPretUnitar(BigDecimal pretUnitar) { this.pretUnitar = pretUnitar; }

    public String getMoneda() { return moneda; }
    public void setMoneda(String moneda) { this.moneda = moneda; }

    public BigDecimal getCotaTva() { return cotaTva; }
    public void setCotaTva(BigDecimal cotaTva) { this.cotaTva = cotaTva; }
}
