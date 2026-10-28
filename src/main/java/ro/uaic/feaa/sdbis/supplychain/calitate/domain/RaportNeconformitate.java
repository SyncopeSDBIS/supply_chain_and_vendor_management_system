package ro.uaic.feaa.sdbis.supplychain.calitate.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import ro.uaic.feaa.sdbis.supplychain.calitate.domain.event.NCREscaladatEvent;
import ro.uaic.feaa.sdbis.supplychain.shared.IdFurnizor;
import ro.uaic.feaa.sdbis.supplychain.shared.IdLot;
import ro.uaic.feaa.sdbis.supplychain.shared.annotation.AggregateRoot;

import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@AggregateRoot
@Entity
@Table(name = "calitate_ncr")
public class RaportNeconformitate implements Serializable {

    @NotNull
    @Id
    @Column(nullable = false)
    private UUID idNCR;

    @NotNull
    @Column(name = "inspectie_id", nullable = false)
    private UUID inspectieId;

    @NotNull
    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "furnizor_id", nullable = false))
    private IdFurnizor furnizorId;

    @NotNull
    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "lot_id", nullable = false))
    private IdLot lotId;

    @Column(name = "cauza_radacina")
    private String cauzaRadacina;

    @Embedded
    private ActiuneCorectiva actiuneCorectiva;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StareNCR stareNCR;

    protected RaportNeconformitate() {}

    public RaportNeconformitate(UUID idNCR, UUID inspectieId, IdFurnizor furnizorId, IdLot lotId) {
        this.idNCR = Objects.requireNonNull(idNCR, "idNCR cannot be null");
        this.inspectieId = Objects.requireNonNull(inspectieId, "inspectieId cannot be null");
        this.furnizorId = Objects.requireNonNull(furnizorId, "furnizorId cannot be null");
        this.lotId = Objects.requireNonNull(lotId, "lotId cannot be null");
        this.stareNCR = StareNCR.Deschis;
    }

    public void analizeazaCauza(String cauza) {
        this.cauzaRadacina = Objects.requireNonNull(cauza, "cauza cannot be null");
        this.stareNCR = StareNCR.InAnalizaCauzei;
    }

    public void initiazaActiuneCorectiva(ActiuneCorectiva a) {
        this.actiuneCorectiva = Objects.requireNonNull(a, "actiuneCorectiva cannot be null");
        this.stareNCR = StareNCR.ActiuneCorectivaInCurs;
    }

    public void verifica() {
        this.stareNCR = StareNCR.Verificat;
    }

    public void inchide() {
        this.stareNCR = StareNCR.Inchis;
    }

    public NCREscaladatEvent escaladeaza() {
        this.stareNCR = StareNCR.Escaladat;
        return new NCREscaladatEvent(UUID.randomUUID(), Instant.now(), this.idNCR, this.furnizorId);
    }

    public UUID getIdNCR() { return idNCR; }
    public UUID getInspectieId() { return inspectieId; }
    public IdFurnizor getFurnizorId() { return furnizorId; }
    public IdLot getLotId() { return lotId; }
    public String getCauzaRadacina() { return cauzaRadacina; }
    public ActiuneCorectiva getActiuneCorectiva() { return actiuneCorectiva; }
    public StareNCR getStareNCR() { return stareNCR; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RaportNeconformitate that)) return false;
        return Objects.equals(idNCR, that.idNCR);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idNCR);
    }

    @Override
    public String toString() {
        return "RaportNeconformitate[id=" + idNCR + ", stare=" + stareNCR + ", lot=" + lotId + "]";
    }
}
