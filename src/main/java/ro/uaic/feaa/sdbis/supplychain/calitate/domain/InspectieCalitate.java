package ro.uaic.feaa.sdbis.supplychain.calitate.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import ro.uaic.feaa.sdbis.supplychain.calitate.domain.event.InspectieProgramataEvent;
import ro.uaic.feaa.sdbis.supplychain.calitate.domain.event.VerdictInspectieEvent;
import ro.uaic.feaa.sdbis.supplychain.shared.CodMaterial;
import ro.uaic.feaa.sdbis.supplychain.shared.IdComanda;
import ro.uaic.feaa.sdbis.supplychain.shared.IdFurnizor;
import ro.uaic.feaa.sdbis.supplychain.shared.IdLot;
import ro.uaic.feaa.sdbis.supplychain.shared.Perioada;
import ro.uaic.feaa.sdbis.supplychain.shared.annotation.AggregateRoot;

import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * Aggregate Root pentru inspectia de calitate AQL (Figura 20 si 23).
 */
@AggregateRoot
@Entity
@Table(name = "calitate_inspectii")
public class InspectieCalitate implements Serializable {

    @NotNull
    @Id
    @Column(name = "id_inspectie", nullable = false)
    private UUID idInspectie;

    @NotNull
    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "lot_id", nullable = false))
    private IdLot lotId;

    @NotNull
    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "comanda_id", nullable = false))
    private IdComanda comandaId;

    @NotNull
    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "furnizor_id", nullable = false))
    private IdFurnizor furnizorId;

    @NotNull
    @Embedded
    @AttributeOverride(name = "cod", column = @Column(name = "cod_material", nullable = false, length = 50))
    private CodMaterial material;

    @NotNull
    @Embedded
    @AttributeOverride(name = "nivelAQL", column = @Column(name = "nivel_aql", precision = 5, scale = 2))
    @AttributeOverride(name = "dimensiuneEsantion", column = @Column(name = "dimensiune_esantion"))
    @AttributeOverride(name = "numarAcceptare", column = @Column(name = "numar_acceptare"))
    @AttributeOverride(name = "numarRespingere", column = @Column(name = "numar_respingere"))
    private PlanAQL plan;

    @NotNull
    @Embedded
    @AttributeOverride(name = "dataInceput", column = @Column(name = "data_inceput", nullable = false))
    @AttributeOverride(name = "dataFinalizare", column = @Column(name = "data_finalizare", nullable = false))
    private Perioada perioadaPlanificata;

    @Column(name = "inspector_id")
    private UUID inspectorId;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "stare_inspectie", nullable = false, length = 50)
    private StareInspectie stareInspectie;

    @Enumerated(EnumType.STRING)
    @Column(name = "verdict", length = 50)
    private Verdict verdict;

    @Version
    @Column(name = "version", nullable = false)
    private Long version = 0L;

    protected InspectieCalitate() {}

    public InspectieCalitate(UUID idInspectie, IdLot lotId, IdComanda comandaId, IdFurnizor furnizorId,
                            CodMaterial material, PlanAQL plan, Perioada perioadaPlanificata) {
        this.idInspectie = Objects.requireNonNull(idInspectie);
        this.lotId = Objects.requireNonNull(lotId);
        this.comandaId = Objects.requireNonNull(comandaId);
        this.furnizorId = Objects.requireNonNull(furnizorId);
        this.material = Objects.requireNonNull(material);
        this.plan = Objects.requireNonNull(plan);
        this.perioadaPlanificata = Objects.requireNonNull(perioadaPlanificata);
        this.stareInspectie = StareInspectie.Programata;
    }

    public static InspectieCalitate programeaza(IdLot lotId, IdComanda comandaId, IdFurnizor furnizorId,
                                                CodMaterial material, PlanAQL plan, Perioada p) {
        return new InspectieCalitate(UUID.randomUUID(), lotId, comandaId, furnizorId, material, plan, p);
    }

    public void aloca(UUID inspectorId) {
        if (this.stareInspectie != StareInspectie.Programata) {
            throw new IllegalStateException("Inspectia poate fi alocata doar cand este Programata. Stare: " + stareInspectie);
        }
        this.inspectorId = Objects.requireNonNull(inspectorId);
        this.stareInspectie = StareInspectie.InspectorAlocat;
    }

    public void incepe() {
        if (this.stareInspectie != StareInspectie.InspectorAlocat && this.stareInspectie != StareInspectie.InReinspectie) {
            throw new IllegalStateException("Inspectia nu poate fi inceputa in starea: " + stareInspectie);
        }
        this.stareInspectie = StareInspectie.InDesfasurare;
    }

    public VerdictInspectieEvent inregistreazaVerdict(int defecteGasite) {
        if (this.stareInspectie != StareInspectie.InDesfasurare) {
            throw new IllegalStateException("Verdictul se poate inregistra doar cand inspectia este InDesfasurare");
        }
        this.verdict = this.plan.decide(defecteGasite);
        if (this.verdict == Verdict.Conforma) {
            this.stareInspectie = StareInspectie.Conforma;
        } else {
            this.stareInspectie = StareInspectie.Neconforma;
        }

        return new VerdictInspectieEvent(
            UUID.randomUUID(), Instant.now(), this.lotId, this.comandaId, this.verdict
        );
    }

    public void solicitaReinspectie() {
        if (this.stareInspectie != StareInspectie.Neconforma) {
            throw new IllegalStateException("Doar inspectiile neconforme pot intra in reinspectie");
        }
        this.stareInspectie = StareInspectie.InReinspectie;
    }

    public void inchide() {
        this.stareInspectie = StareInspectie.Inchisa;
    }

    public void anuleaza() {
        this.stareInspectie = StareInspectie.Anulata;
    }

    public UUID getIdInspectie() { return idInspectie; }
    public IdLot getLotId() { return lotId; }
    public IdComanda getComandaId() { return comandaId; }
    public IdFurnizor getFurnizorId() { return furnizorId; }
    public CodMaterial getMaterial() { return material; }
    public PlanAQL getPlan() { return plan; }
    public Perioada getPerioadaPlanificata() { return perioadaPlanificata; }
    public UUID getInspectorId() { return inspectorId; }
    public StareInspectie getStareInspectie() { return stareInspectie; }
    public Verdict getVerdict() { return verdict; }
    public Long getVersion() { return version; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof InspectieCalitate that)) return false;
        return getIdInspectie() != null && Objects.equals(getIdInspectie(), that.getIdInspectie());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "InspectieCalitate[id=" + idInspectie + ", lot=" + lotId + ", stare=" + stareInspectie + ", verdict=" + verdict + "]";
    }
}
