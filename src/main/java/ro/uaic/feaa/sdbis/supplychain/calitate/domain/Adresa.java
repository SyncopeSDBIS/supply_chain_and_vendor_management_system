package ro.uaic.feaa.sdbis.supplychain.calitate.domain;

import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class Adresa implements Serializable {
    private String strada;
    private String numar;
    private String localitate;
    private String codPostal;

    protected Adresa() {}

    public Adresa(String strada, String numar, String localitate, String codPostal) {
        this.strada = Objects.requireNonNull(strada);
        this.numar = Objects.requireNonNull(numar);
        this.localitate = Objects.requireNonNull(localitate);
        this.codPostal = Objects.requireNonNull(codPostal);
    }

    public String getStrada() { return strada; }
    public String getNumar() { return numar; }
    public String getLocalitate() { return localitate; }
    public String getCodPostal() { return codPostal; }
}
