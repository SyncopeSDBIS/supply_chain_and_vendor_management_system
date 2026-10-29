package ro.uaic.feaa.sdbis.supplychain.calitate.presentation;

import jakarta.validation.constraints.*;
import java.io.Serializable;

/**
 * Request DTO for supplier registration with declarative validation constraints.
 */
public class InregistreazaFurnizorRequest implements Serializable {

    @NotBlank(message = "Codul fiscal este obligatoriu")
    @Size(min = 2, max = 30, message = "Codul fiscal trebuie sa aiba intre 2 si 30 caractere")
    private String codFiscal;

    @NotBlank(message = "Denumirea furnizorului este obligatorie")
    @Size(min = 2, max = 255, message = "Denumirea trebuie sa contina intre 2 si 255 caractere")
    private String denumire;

    @Pattern(regexp = "^[A-Z]{2}[0-9]{2}[A-Z0-9]{4,30}$", message = "Format IBAN invalid")
    private String iban;

    @Email(message = "Formatul adresei de email este invalid")
    private String email;

    public InregistreazaFurnizorRequest() {}

    public InregistreazaFurnizorRequest(String codFiscal, String denumire, String iban, String email) {
        this.codFiscal = codFiscal;
        this.denumire = denumire;
        this.iban = iban;
        this.email = email;
    }

    public String getCodFiscal() { return codFiscal; }
    public void setCodFiscal(String codFiscal) { this.codFiscal = codFiscal; }

    public String getDenumire() { return denumire; }
    public void setDenumire(String denumire) { this.denumire = denumire; }

    public String getIban() { return iban; }
    public void setIban(String iban) { this.iban = iban; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}

