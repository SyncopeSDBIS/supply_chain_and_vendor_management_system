package ro.uaic.feaa.sdbis.supplychain.calitate.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import ro.uaic.feaa.sdbis.supplychain.shared.annotation.ValueObject;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

@ValueObject
@Embeddable
public final class ActiuneCorectiva implements Serializable {

    @NotBlank
    @Column(name = "descriere_actiune")
    private String descriere;

    @NotBlank
    @Column(name = "responsabil", length = 100)
    private String responsabil;

    @NotNull
    @Column(name = "termen")
    private LocalDate termen;

    protected ActiuneCorectiva() {}

    public ActiuneCorectiva(String descriere, String responsabil, LocalDate termen) {
        if (descriere == null || descriere.trim().isEmpty()) {
            throw new IllegalArgumentException("Descriere cannot be null or empty");
        }
        if (responsabil == null || responsabil.trim().isEmpty()) {
            throw new IllegalArgumentException("Responsabil cannot be null or empty");
        }
        this.descriere = descriere.trim();
        this.responsabil = responsabil.trim();
        this.termen = Objects.requireNonNull(termen, "termen cannot be null");
    }

    public String getDescriere() { return descriere; }
    public String getResponsabil() { return responsabil; }
    public LocalDate getTermen() { return termen; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ActiuneCorectiva that)) return false;
        return Objects.equals(descriere, that.descriere) &&
               Objects.equals(responsabil, that.responsabil) &&
               Objects.equals(termen, that.termen);
    }

    @Override
    public int hashCode() {
        return Objects.hash(descriere, responsabil, termen);
    }

    @Override
    public String toString() {
        return descriere + " (Responsabil: " + responsabil + ", Termen: " + termen + ")";
    }
}
