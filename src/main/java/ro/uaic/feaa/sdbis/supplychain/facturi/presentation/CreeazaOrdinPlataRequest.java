package ro.uaic.feaa.sdbis.supplychain.facturi.presentation;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.UUID;

/**
 * Request DTO for payment order creation.
 */
public class CreeazaOrdinPlataRequest implements Serializable {

    @Positive(message = "Suma ordinului de plata trebuie sa fie strict pozitiva")
    private double suma;

    @NotBlank(message = "Moneda este obligatorie")
    @Pattern(regexp = "EUR|RON|USD", message = "Moneda trebuie sa fie una dintre: EUR, RON, USD")
    private String moneda = "EUR";

    @NotNull(message = "Identificatorul utilizatorului creator este obligatoriu")
    private UUID creatDe;

    public CreeazaOrdinPlataRequest() {}

    public CreeazaOrdinPlataRequest(double suma, String moneda, UUID creatDe) {
        this.suma = suma;
        this.moneda = moneda != null ? moneda : "EUR";
        this.creatDe = creatDe;
    }

    public double getSuma() { return suma; }
    public void setSuma(double suma) { this.suma = suma; }

    public String getMoneda() { return moneda; }
    public void setMoneda(String moneda) { this.moneda = moneda; }

    public UUID getCreatDe() { return creatDe; }
    public void setCreatDe(UUID creatDe) { this.creatDe = creatDe; }
}

