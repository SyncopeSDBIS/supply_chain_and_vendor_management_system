package ro.uaic.feaa.sdbis.supplychain.calitate.domain;

import jakarta.persistence.*;
import ro.uaic.feaa.sdbis.supplychain.shared.Perioada;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

/**
 * Entitate componenta a agregatului Furnizor.
 */
@Entity
@Table(name = "calitate_scorecards")
public class ScorecardLunar implements Serializable {

    @Id
    private UUID id;

    @Embedded
    @AttributeOverride(name = "dataInceput", column = @Column(name = "luna_inceput"))
    @AttributeOverride(name = "dataFinalizare", column = @Column(name = "luna_sfarsit"))
    private Perioada luna;

    private BigDecimal otif;
    private BigDecimal rataRespingere;
    private BigDecimal conformitatePret;

    @Embedded
    @AttributeOverride(name = "valoare", column = @Column(name = "scor_valoare"))
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
}
