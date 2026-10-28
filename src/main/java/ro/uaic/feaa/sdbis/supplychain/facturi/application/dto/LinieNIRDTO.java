package ro.uaic.feaa.sdbis.supplychain.facturi.application.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

import java.io.Serializable;
import java.math.BigDecimal;

@XmlRootElement(name = "linieNIR")
@XmlAccessorType(XmlAccessType.FIELD)
@JsonRootName("linieNIR")
public class LinieNIRDTO implements Serializable {

    @XmlElement
    @JsonProperty("materialCod")
    private String materialCod;

    @XmlElement
    @JsonProperty("cantitateComandata")
    private BigDecimal cantitateComandata;

    @XmlElement
    @JsonProperty("cantitateReceptionata")
    private BigDecimal cantitateReceptionata;

    @XmlElement
    @JsonProperty("cantitateAcceptata")
    private BigDecimal cantitateAcceptata;

    @XmlElement
    @JsonProperty("cantitateRespinsa")
    private BigDecimal cantitateRespinsa;

    @XmlElement
    @JsonProperty("pretUnitar")
    private BigDecimal pretUnitar;

    @XmlElement
    @JsonProperty("moneda")
    private String moneda;

    public LinieNIRDTO() {}

    public LinieNIRDTO(String materialCod, BigDecimal cantitateComandata, BigDecimal cantitateReceptionata,
                       BigDecimal cantitateAcceptata, BigDecimal cantitateRespinsa, BigDecimal pretUnitar, String moneda) {
        this.materialCod = materialCod;
        this.cantitateComandata = cantitateComandata;
        this.cantitateReceptionata = cantitateReceptionata;
        this.cantitateAcceptata = cantitateAcceptata;
        this.cantitateRespinsa = cantitateRespinsa;
        this.pretUnitar = pretUnitar;
        this.moneda = moneda;
    }

    public String getMaterialCod() { return materialCod; }
    public void setMaterialCod(String materialCod) { this.materialCod = materialCod; }

    public BigDecimal getCantitateComandata() { return cantitateComandata; }
    public void setCantitateComandata(BigDecimal cantitateComandata) { this.cantitateComandata = cantitateComandata; }

    public BigDecimal getCantitateReceptionata() { return cantitateReceptionata; }
    public void setCantitateReceptionata(BigDecimal cantitateReceptionata) { this.cantitateReceptionata = cantitateReceptionata; }

    public BigDecimal getCantitateAcceptata() { return cantitateAcceptata; }
    public void setCantitateAcceptata(BigDecimal cantitateAcceptata) { this.cantitateAcceptata = cantitateAcceptata; }

    public BigDecimal getCantitateRespinsa() { return cantitateRespinsa; }
    public void setCantitateRespinsa(BigDecimal cantitateRespinsa) { this.cantitateRespinsa = cantitateRespinsa; }

    public BigDecimal getPretUnitar() { return pretUnitar; }
    public void setPretUnitar(BigDecimal pretUnitar) { this.pretUnitar = pretUnitar; }

    public String getMoneda() { return moneda; }
    public void setMoneda(String moneda) { this.moneda = moneda; }
}
