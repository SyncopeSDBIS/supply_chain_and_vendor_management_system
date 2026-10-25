package ro.uaic.feaa.sdbis.supplychain.comenzi.application.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.math.BigDecimal;

@XmlRootElement(name = "linieSumar")
@XmlAccessorType(XmlAccessType.FIELD)
public class LinieSumarDTO implements Serializable {

    @XmlElement
    @JsonProperty("material")
    private String material;

    @XmlElement
    @JsonProperty("cantitateComandata")
    private BigDecimal cantitateComandata;

    @XmlElement
    @JsonProperty("pretUnitar")
    private BigDecimal pretUnitar;

    public LinieSumarDTO() {}

    public LinieSumarDTO(String material, BigDecimal cantitateComandata, BigDecimal pretUnitar) {
        this.material = material;
        this.cantitateComandata = cantitateComandata;
        this.pretUnitar = pretUnitar;
    }

    public String getMaterial() { return material; }
    public void setMaterial(String material) { this.material = material; }

    public BigDecimal getCantitateComandata() { return cantitateComandata; }
    public void setCantitateComandata(BigDecimal cantitateComandata) { this.cantitateComandata = cantitateComandata; }

    public BigDecimal getPretUnitar() { return pretUnitar; }
    public void setPretUnitar(BigDecimal pretUnitar) { this.pretUnitar = pretUnitar; }
}
