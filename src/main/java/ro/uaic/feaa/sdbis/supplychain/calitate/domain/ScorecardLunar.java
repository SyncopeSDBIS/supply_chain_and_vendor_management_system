package ro.uaic.feaa.sdbis.supplychain.calitate.domain;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.persistence.Version;
import jakarta.validation.constraints.NotNull;
import ro.uaic.feaa.sdbis.supplychain.shared.Perioada;
import ro.uaic.feaa.sdbis.supplychain.shared.PerformantaLivrariVO;
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
@Table(
    name = "calitate_scorecards",
    uniqueConstraints = @UniqueConstraint(name = "uq_scorecards_furnizor_perioada", columnNames = {"furnizor_id", "data_inceput", "data_finalizare"})
)
public class ScorecardLunar implements Serializable {
    private static final long serialVersionUID = 1L;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "furnizor_id")
    private Furnizor furnizor;

    @Version
    @Column(name = "version")
    private Long version;

    @NotNull
    @Id
    @Column(name = "id", nullable = false)
    private UUID id;

    @NotNull
    @Embedded
    @AttributeOverride(name = "dataInceput", column = @Column(name = "data_inceput", nullable = false))
    @AttributeOverride(name = "dataFinalizare", column = @Column(name = "data_finalizare", nullable = false))
    private Perioada luna;

    @NotNull
    @Column(name = "otif", nullable = false, precision = 5, scale = 2)
    private BigDecimal otif;

    @NotNull
    @Column(name = "rata_respingere", nullable = false, precision = 5, scale = 2)
    private BigDecimal rataRespingere;

    @NotNull
    @Column(name = "conformitate_pret", nullable = false, precision = 5, scale = 2)
    private BigDecimal conformitatePret;

    @NotNull
    @Embedded
    @AttributeOverride(name = "valoare", column = @Column(name = "scor", nullable = false, precision = 5, scale = 2))
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

    void ataseazaFurnizor(Furnizor furnizor) {
        this.furnizor = furnizor;
    }

    public Furnizor getFurnizor() {
        return furnizor;
    }

    public UUID getId() { return id; }
    public Perioada getLuna() { return luna; }
    public BigDecimal getOtif() { return otif; }
    public BigDecimal getRataRespingere() { return rataRespingere; }
    public BigDecimal getConformitatePret() { return conformitatePret; }
    public Scor getScor() { return scor; }
    public Long getVersion() { return version; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ScorecardLunar that)) return false;
        return getId() != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "ScorecardLunar[id=" + id + ", luna=" + luna + ", scor=" + scor + "]";
    }
}
