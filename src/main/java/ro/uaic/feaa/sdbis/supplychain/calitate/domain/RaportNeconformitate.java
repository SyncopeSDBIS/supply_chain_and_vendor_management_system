package ro.uaic.feaa.sdbis.supplychain.calitate.domain;

import jakarta.persistence.*;
import ro.uaic.feaa.sdbis.supplychain.calitate.domain.event.NCREscaladatEvent;
import ro.uaic.feaa.sdbis.supplychain.shared.IdFurnizor;
import ro.uaic.feaa.sdbis.supplychain.shared.IdLot;

import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "calitate_ncr")
public class RaportNeconformitate implements Serializable {

    @Id
    private UUID idNCR;

    private UUID inspectieId;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "furnizor_id"))
    private IdFurnizor furnizorId;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "lot_id"))
    private IdLot lotId;

    private String cauzaRadacina;

    @Embedded
    private ActiuneCorectiva actiuneCorectiva;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StareNCR stareNCR;

    protected RaportNeconformitate() {}

    public RaportNeconformitate(UUID idNCR, UUID inspectieId, IdFurnizor furnizorId, IdLot lotId) {
        this.idNCR = Objects.requireNonNull(idNCR);
        this.inspectieId = Objects.requireNonNull(inspectieId);
        this.furnizorId = Objects.requireNonNull(furnizorId);
        this.lotId = Objects.requireNonNull(lotId);
        this.stareNCR = StareNCR.Deschis;
    }

    public void analizeazaCauza(String cauza) {
        this.cauzaRadacina = Objects.requireNonNull(cauza);
        this.stareNCR = StareNCR.InAnalizaCauzei;
    }

    public void initiazaActiuneCorectiva(ActiuneCorectiva a) {
        this.actiuneCorectiva = Objects.requireNonNull(a);
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
}
