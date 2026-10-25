package ro.uaic.feaa.sdbis.supplychain.calitate.domain;

import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public final class Iban implements Serializable {
    private String valoare;

    protected Iban() {}

    public Iban(String valoare) {
        if (!esteValid(valoare)) {
            throw new IllegalArgumentException("IBAN invalid: " + valoare);
        }
        this.valoare = valoare.replaceAll("\s+", "").toUpperCase();
    }

    public static boolean esteValid(String v) {
        if (v == null) return false;
        String clean = v.replaceAll("\s+", "").toUpperCase();
        return clean.length() >= 15 && clean.length() <= 34 && Character.isLetter(clean.charAt(0)) && Character.isLetter(clean.charAt(1));
    }

    public static Iban of(String v) {
        return new Iban(v);
    }

    public String getValoare() { return valoare; }

    @Override
    public String toString() { return valoare; }
}
