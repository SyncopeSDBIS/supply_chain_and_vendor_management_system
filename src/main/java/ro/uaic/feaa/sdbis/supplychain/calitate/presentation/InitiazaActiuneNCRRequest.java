package ro.uaic.feaa.sdbis.supplychain.calitate.presentation;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.io.Serializable;

/**
 * Request DTO for initiating corrective action on an NCR.
 */
public class InitiazaActiuneNCRRequest implements Serializable {

    @NotBlank(message = "Descrierea actiunii corective este obligatorie")
    @Size(min = 5, max = 500, message = "Descrierea trebuie sa contina intre 5 si 500 caractere")
    private String descriere;

    @NotBlank(message = "Responsabilul este obligatoriu")
    @Size(min = 2, max = 100, message = "Responsabilul trebuie sa contina intre 2 si 100 caractere")
    private String responsabil;

    @NotBlank(message = "Termenul de implementare este obligatoriu")
    private String termen;

    public InitiazaActiuneNCRRequest() {}

    public InitiazaActiuneNCRRequest(String descriere, String responsabil, String termen) {
        this.descriere = descriere;
        this.responsabil = responsabil;
        this.termen = termen;
    }

    public String getDescriere() { return descriere; }
    public void setDescriere(String descriere) { this.descriere = descriere; }

    public String getResponsabil() { return responsabil; }
    public void setResponsabil(String responsabil) { this.responsabil = responsabil; }

    public String getTermen() { return termen; }
    public void setTermen(String termen) { this.termen = termen; }
}

