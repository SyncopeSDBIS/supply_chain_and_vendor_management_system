package ro.uaic.feaa.sdbis.supplychain.calitate.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotBlank;
import ro.uaic.feaa.sdbis.supplychain.shared.annotation.ValueObject;
import java.io.Serializable;
import java.util.Objects;

@ValueObject
@Embeddable
public final class Iban implements Serializable {

    @NotBlank
    @Column(name = "valoare", nullable = false)
    private String valoare;

    protected Iban() {}

    public Iban(String valoare) {
        if (!esteValid(valoare)) {
            throw new IllegalArgumentException("IBAN invalid: " + valoare);
        }
        this.valoare = valoare.replaceAll("\\s+", "").toUpperCase();
    }

    public static boolean esteValid(String v) {
        if (v == null) return false;
        String clean = v.replaceAll("\\s+", "").toUpperCase();
        return clean.length() >= 15 && clean.length() <= 34 && Character.isLetter(clean.charAt(0)) && Character.isLetter(clean.charAt(1));
    }

    public static Iban of(String v) {
        return new Iban(v);
    }

    public String getValoare() { return valoare; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Iban iban)) return false;
        return Objects.equals(valoare, iban.valoare);
    }

    @Override
    public int hashCode() {
        return Objects.hash(valoare);
    }

    @Override
    public String toString() { return valoare; }
}
