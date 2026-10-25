package ro.uaic.feaa.sdbis.supplychain.calitate.domain;

import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

@Embeddable
public class ActiuneCorectiva implements Serializable {
    private String descriere;
    private String responsabil;
    private LocalDate termen;

    protected ActiuneCorectiva() {}

    public ActiuneCorectiva(String descriere, String responsabil, LocalDate termen) {
        this.descriere = Objects.requireNonNull(descriere, "descriere cannot be null");
        this.responsabil = Objects.requireNonNull(responsabil, "responsabil cannot be null");
        this.termen = Objects.requireNonNull(termen, "termen cannot be null");
    }

    public String getDescriere() { return descriere; }
    public String getResponsabil() { return responsabil; }
    public LocalDate getTermen() { return termen; }
}
