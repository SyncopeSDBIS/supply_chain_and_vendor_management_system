package ro.uaic.feaa.sdbis.supplychain.calitate.presentation;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

    private List<String> emailuri;

    private Set<String> categorii = new HashSet<>();

    private AdresaDTO adresa;

    private Integer termenPlataZile;

    private String moneda;

    public static class AdresaDTO implements Serializable {
        private String strada;
        private String numar;
        private String localitate;
        private String oras;
        private String codPostal;
        private String tara;

        public AdresaDTO() {}

        public String getStrada() { return strada; }
        public void setStrada(String strada) { this.strada = strada; }

        public String getNumar() { return numar; }
        public void setNumar(String numar) { this.numar = numar; }

        public String getLocalitate() { return localitate != null && !localitate.isBlank() ? localitate : oras; }
        public void setLocalitate(String localitate) { this.localitate = localitate; }

        public String getOras() { return oras; }
        public void setOras(String oras) { this.oras = oras; }

        public String getCodPostal() { return codPostal; }
        public void setCodPostal(String codPostal) { this.codPostal = codPostal; }

        public String getTara() { return tara; }
        public void setTara(String tara) { this.tara = tara; }
    }

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

    public List<String> getEmailuri() { return emailuri; }
    public void setEmailuri(List<String> emailuri) { this.emailuri = emailuri; }

    public Set<String> getCategorii() { return categorii; }
    public void setCategorii(Set<String> categorii) { this.categorii = categorii; }

    public AdresaDTO getAdresa() { return adresa; }
    public void setAdresa(AdresaDTO adresa) { this.adresa = adresa; }

    public Integer getTermenPlataZile() { return termenPlataZile; }
    public void setTermenPlataZile(Integer termenPlataZile) { this.termenPlataZile = termenPlataZile; }

    public String getMoneda() { return moneda; }
    public void setMoneda(String moneda) { this.moneda = moneda; }
}
