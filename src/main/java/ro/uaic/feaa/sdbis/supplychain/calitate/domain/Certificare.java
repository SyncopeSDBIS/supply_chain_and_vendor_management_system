package ro.uaic.feaa.sdbis.supplychain.calitate.domain;

import jakarta.persistence.Embeddable;
import ro.uaic.feaa.sdbis.supplychain.shared.Perioada;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

@Embeddable
public class Certificare implements Serializable {
    private String denumire;
    private LocalDate dataExpirare;

    protected Certificare() {}

    public Certificare(String denumire, LocalDate dataExpirare) {
        this.denumire = Objects.requireNonNull(denumire, "denumire cannot be null");
        this.dataExpirare = Objects.requireNonNull(dataExpirare, "dataExpirare cannot be null");
    }

    public boolean esteValidaPe(Perioada p) {
        Objects.requireNonNull(p, "perioada cannot be null");
        return !dataExpirare.isBefore(p.getDataFinalizare());
    }

    public boolean esteExpirataIn(LocalDate data) {
        return dataExpirare.isBefore(data);
    }

    public String getDenumire() { return denumire; }
    public LocalDate getDataExpirare() { return dataExpirare; }
}
