package ro.uaic.feaa.sdbis.supplychain.calitate.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import ro.uaic.feaa.sdbis.supplychain.shared.Perioada;
import ro.uaic.feaa.sdbis.supplychain.shared.annotation.DomainEntity;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

/**
 * Entitate componenta a agregatului Furnizor.
 */
@DomainEntity
@Entity
@Table(name = "calitate_scorecards")
public class ScorecardLunar implements Serializable {

    @NotNull
    @Id
    @Column(nullable = false)
    private UUID id;

    @NotNull
    @Embedded
    @AttributeOverride(name = "dataInceput", column = @Column(name = "luna_inceput", nullable = false))
    @AttributeOverride(name = "dataFinalizare", column = @Column(name = "luna_sfarsit", nullable = false))
    private Perioada luna;

    @NotNull
    @Column(nullable = false)
    private BigDecimal otif;

    @NotNull
    @Column(nullable = false)
    private BigDecimal rataRespingere;

    @NotNull
    @Column(nullable = false)
    private BigDecimal conformitatePret;

    @NotNull
    @Embedded
    @AttributeOverride(name = "valoare", column = @Column(name = "scor_valoare", nullable = false))
    private Scor scor;

    protected ScorecardLunar() {}

    public ScorecardLunar(UUID id, Perioada luna, BigDecimal otif, BigDecimal rataRespingere, BigDecimal conformitatePret, Scor scor) {
        this.id = Objects.requireNonNull(id, "id cannot be null");
        this.luna = Objects.requireNonNull(luna, "luna cannot be null");
        this.otif = Objects.requireNonNull(otif, "otif cannot be null");
        this.rataRespingere = Objects.requireNonNull(rataRespingere, "rataRespingere cannot be null");
        this.conformitatePret = Objects.requireNonNull(conformitatePret, "conformitatePret cannot be null");
        this.scor = Objects.requireNonNull(scor, "scor cannot be null");
    }

    public static ScorecardLunar calculeaza(Perioada luna, PerformantaLivrariVO perf, BigDecimal rataRespingere) {
        BigDecimal calitate = BigDecimal.valueOf(100.0).subtract(rataRespingere);
        if (calitate.compareTo(BigDecimal.ZERO) < 0) calitate = BigDecimal.ZERO;
        Scor scor = Scor.ponderat(perf.otif(), calitate, perf.conformitatePret());
        return new ScorecardLunar(UUID.randomUUID(), luna, perf.otif(), rataRespingere, perf.conformitatePret(), scor);
    }

    public Calificativ calificativ() {
        return scor.calificativ();
    }

    public UUID getId() { return id; }
    public Perioada getLuna() { return luna; }
    public BigDecimal getOtif() { return otif; }
    public BigDecimal getRataRespingere() { return rataRespingere; }
    public BigDecimal getConformitatePret() { return conformitatePret; }
    public Scor getScor() { return scor; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ScorecardLunar that)) return false;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "ScorecardLunar[id=" + id + ", luna=" + luna + ", scor=" + scor + "]";
    }
}
