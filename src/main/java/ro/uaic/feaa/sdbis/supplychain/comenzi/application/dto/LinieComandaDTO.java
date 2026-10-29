package ro.uaic.feaa.sdbis.supplychain.comenzi.application.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.math.BigDecimal;

@XmlRootElement(name = "linieComanda")
@XmlAccessorType(XmlAccessType.FIELD)
public class LinieComandaDTO implements Serializable {

    @NotBlank(message = "Codul de material este obligatoriu")
    @Size(min = 1, max = 50, message = "Codul de material trebuie sa aiba intre 1 si 50 caractere")
    @XmlElement
    @JsonProperty("materialCod")
    private String materialCod;

    @NotNull(message = "Cantitatea este obligatorie")
    @DecimalMin(value = "0.0001", message = "Cantitatea trebuie sa fie strict pozitiva")
    @XmlElement
    @JsonProperty("cantitate")
    private BigDecimal cantitate;

    @NotBlank(message = "Unitatea de masura este obligatorie")
    @Size(min = 1, max = 20, message = "Unitatea de masura trebuie sa aiba intre 1 si 20 caractere")
    @XmlElement
    @JsonProperty("unitateMasura")
    private String unitateMasura;

    @NotNull(message = "Pretul unitar este obligatoriu")
    @DecimalMin(value = "0.00", message = "Pretul unitar nu poate fi negativ")
    @XmlElement
    @JsonProperty("pretUnitar")
    private BigDecimal pretUnitar;

    @NotBlank(message = "Moneda este obligatorie")
    @Size(min = 3, max = 3, message = "Moneda trebuie sa aiba cod ISO din 3 caractere")
    @XmlElement
    @JsonProperty("moneda")
    private String moneda;

    public LinieComandaDTO() {}

    public LinieComandaDTO(String materialCod, BigDecimal cantitate, String unitateMasura, BigDecimal pretUnitar, String moneda) {
        this.materialCod = materialCod;
        this.cantitate = cantitate;
        this.unitateMasura = unitateMasura;
        this.pretUnitar = pretUnitar;
        this.moneda = moneda;
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
}
