package ro.uaic.feaa.sdbis.supplychain.facturi.presentation;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.UUID;

/**
 * Request DTO for supplier invoice registration with declarative Bean Validation constraints.
 */
public class InregistreazaFacturaRequest implements Serializable {

    @NotBlank(message = "Seria facturii este obligatorie")
    @Size(min = 1, max = 20, message = "Seria facturii trebuie sa contina intre 1 si 20 caractere")
    private String serie;

    @NotBlank(message = "Numarul facturii este obligatoriu")
    @Size(min = 1, max = 50, message = "Numarul facturii trebuie sa contina intre 1 si 50 caractere")
    private String numar;

    @NotBlank(message = "Data emiterii este obligatorie")
    private String dataEmitere;

    @NotBlank(message = "Data scadentei este obligatorie")
    private String dataScadenta;

    @NotNull(message = "Identificatorul comenzii este obligatoriu")
    private UUID comandaId;

    @NotNull(message = "Identificatorul furnizorului este obligatoriu")
    private UUID furnizorId;

    @NotBlank(message = "Moneda este obligatorie")
    @Pattern(regexp = "EUR|RON|USD", message = "Moneda trebuie sa fie una dintre: EUR, RON, USD")
    private String moneda = "EUR";

    @NotBlank(message = "Codul materialului este obligatoriu")
    private String materialCod;

    @Positive(message = "Cantitatea facturata trebuie sa fie strict pozitiva")
    private double cantitate;

    @NotBlank(message = "Unitatea de masura este obligatorie")
    private String unitate;

    @PositiveOrZero(message = "Pretul unitar trebuie sa fie pozitiv sau zero")
    private double pretUnitar;

    public InregistreazaFacturaRequest() {}

    public InregistreazaFacturaRequest(String serie, String numar, String dataEmitere, String dataScadenta,
                                       UUID comandaId, UUID furnizorId, String moneda, String materialCod,
                                       double cantitate, String unitate, double pretUnitar) {
        this.serie = serie;
        this.numar = numar;
        this.dataEmitere = dataEmitere;
        this.dataScadenta = dataScadenta;
        this.comandaId = comandaId;
        this.furnizorId = furnizorId;
        this.moneda = moneda != null ? moneda : "EUR";
        this.materialCod = materialCod;
        this.cantitate = cantitate;
        this.unitate = unitate;
        this.pretUnitar = pretUnitar;
    }

    public String getSerie() { return serie; }
    public void setSerie(String serie) { this.serie = serie; }

    public String getNumar() { return numar; }
    public void setNumar(String numar) { this.numar = numar; }

    public String getDataEmitere() { return dataEmitere; }
    public void setDataEmitere(String dataEmitere) { this.dataEmitere = dataEmitere; }

    public String getDataScadenta() { return dataScadenta; }
    public void setDataScadenta(String dataScadenta) { this.dataScadenta = dataScadenta; }

    public UUID getComandaId() { return comandaId; }
    public void setComandaId(UUID comandaId) { this.comandaId = comandaId; }

    public UUID getFurnizorId() { return furnizorId; }
    public void setFurnizorId(UUID furnizorId) { this.furnizorId = furnizorId; }

    public String getMoneda() { return moneda; }
    public void setMoneda(String moneda) { this.moneda = moneda; }

    public String getMaterialCod() { return materialCod; }
    public void setMaterialCod(String materialCod) { this.materialCod = materialCod; }

    public double getCantitate() { return cantitate; }
    public void setCantitate(double cantitate) { this.cantitate = cantitate; }

    public String getUnitate() { return unitate; }
    public void setUnitate(String unitate) { this.unitate = unitate; }

    public double getPretUnitar() { return pretUnitar; }
    public void setPretUnitar(double pretUnitar) { this.pretUnitar = pretUnitar; }
}

