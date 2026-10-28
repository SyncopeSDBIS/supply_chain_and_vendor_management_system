package ro.uaic.feaa.sdbis.supplychain.calitate.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotBlank;
import ro.uaic.feaa.sdbis.supplychain.shared.annotation.ValueObject;
import java.io.Serializable;
import java.util.Objects;

@ValueObject
@Embeddable
public final class Adresa implements Serializable {

    @NotBlank
    @Column(name = "strada")
    private String strada;

    @NotBlank
    @Column(name = "numar")
    private String numar;

    @NotBlank
    @Column(name = "localitate")
    private String localitate;

    @NotBlank
    @Column(name = "cod_postal")
    private String codPostal;

    protected Adresa() {}

    public Adresa(String strada, String numar, String localitate, String codPostal) {
        if (strada == null || strada.trim().isEmpty()) {
            throw new IllegalArgumentException("Strada cannot be null or empty");
        }
        if (numar == null || numar.trim().isEmpty()) {
            throw new IllegalArgumentException("Numar cannot be null or empty");
        }
        if (localitate == null || localitate.trim().isEmpty()) {
            throw new IllegalArgumentException("Localitate cannot be null or empty");
        }
        if (codPostal == null || codPostal.trim().isEmpty()) {
            throw new IllegalArgumentException("Cod postal cannot be null or empty");
        }
        this.strada = strada.trim();
        this.numar = numar.trim();
        this.localitate = localitate.trim();
        this.codPostal = codPostal.trim();
    }

    public String getStrada() { return strada; }
    public String getNumar() { return numar; }
    public String getLocalitate() { return localitate; }
    public String getCodPostal() { return codPostal; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Adresa adresa)) return false;
        return Objects.equals(strada, adresa.strada) &&
               Objects.equals(numar, adresa.numar) &&
               Objects.equals(localitate, adresa.localitate) &&
               Objects.equals(codPostal, adresa.codPostal);
    }

    @Override
    public int hashCode() {
        return Objects.hash(strada, numar, localitate, codPostal);
    }

    @Override
    public String toString() {
        return strada + " nr. " + numar + ", " + localitate + " (" + codPostal + ")";
    }
}
