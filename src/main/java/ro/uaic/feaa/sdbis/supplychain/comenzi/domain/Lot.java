package ro.uaic.feaa.sdbis.supplychain.comenzi.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import ro.uaic.feaa.sdbis.supplychain.shared.CodMaterial;
import ro.uaic.feaa.sdbis.supplychain.shared.IdLot;
import ro.uaic.feaa.sdbis.supplychain.shared.annotation.DomainEntity;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

/**
 * Entitate componenta a agregatului ComandaAprovizionare.
 * Metodele de mutatie sunt exclusiv package-private (~) conform Tabelelor 2.1 din Project_OOAD.docx.
 */
@DomainEntity
@Entity
@Table(name = "comenzi_loturi")
public class Lot implements Serializable {

    @NotNull
    @EmbeddedId
    private IdLot idLot;

    @NotBlank
    @Size(min = 1, max = 100)
    @Column(name = "cod_lot", nullable = false, length = 100)
    private String codLot;

    @NotNull
    @Embedded
    @AttributeOverride(name = "cod", column = @Column(name = "cod_material", nullable = false, length = 50))
    private CodMaterial material;

    @NotNull
    @Embedded
    @AttributeOverride(name = "valoare", column = @Column(name = "cantitate", nullable = false, precision = 18, scale = 4))
    @AttributeOverride(name = "unitateMasura", column = @Column(name = "unitate_masura", nullable = false, length = 20))
    private Cantitate cantitate;

    @NotNull
    @Embedded
    @AttributeOverride(name = "valoare", column = @Column(name = "cantitate_primita", nullable = false, precision = 18, scale = 4))
    @AttributeOverride(name = "unitateMasura", column = @Column(name = "unitate_masura", insertable = false, updatable = false))
    private Cantitate cantitatePrimita;

    @NotNull
    @Column(name = "data_productie", nullable = false)
    private LocalDate dataProductie;

    @NotNull
    @Column(name = "data_expirare", nullable = false)
    private LocalDate dataExpirare;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "stare_lot", nullable = false, length = 50)
    private StareLot stareLot;

    protected Lot() {}

    Lot(IdLot idLot, String codLot, CodMaterial material, Cantitate cantitate, LocalDate dataProductie, LocalDate dataExpirare) {
        this.idLot = Objects.requireNonNull(idLot, "idLot cannot be null");
        this.codLot = Objects.requireNonNull(codLot, "codLot cannot be null");
        this.material = Objects.requireNonNull(material, "material cannot be null");
        this.cantitate = Objects.requireNonNull(cantitate, "cantitate cannot be null");
        this.cantitatePrimita = Cantitate.zero(cantitate.getUnitateMasura());
        this.dataProductie = dataProductie;
        this.dataExpirare = dataExpirare;
        this.stareLot = StareLot.Planificat;
    }

    // Metode de tranzitie package-private (~)
    void expediaza() {
        if (this.stareLot != StareLot.Planificat) {
            throw new IllegalStateException("Lot must be Planificat to be shipped. Current: " + stareLot);
        }
        this.stareLot = StareLot.Expediat;
    }

    void receptioneaza(Cantitate primit) {
        Objects.requireNonNull(primit, "cantitate primita cannot be null");
        this.cantitatePrimita = this.cantitatePrimita.adauga(primit);
        this.stareLot = StareLot.SositInDepozit;
    }

    void plaseazaInCarantina() {
        this.stareLot = StareLot.InCarantina;
    }

    void elibereazaInStoc() {
        if (this.stareLot != StareLot.InCarantina && this.stareLot != StareLot.SositInDepozit) {
            throw new IllegalStateException("Lot must be InCarantina or SositInDepozit to be released. Current: " + stareLot);
        }
        this.stareLot = StareLot.EliberatInStoc;
    }

    void respinge() {
        if (this.stareLot != StareLot.InCarantina && this.stareLot != StareLot.SositInDepozit) {
            throw new IllegalStateException("Lot must be InCarantina or SositInDepozit to be rejected. Current: " + stareLot);
        }
        this.stareLot = StareLot.Respins;
    }

    void returneazaFurnizorului() {
        if (this.stareLot != StareLot.Respins) {
            throw new IllegalStateException("Only rejected lots can be returned. Current: " + stareLot);
        }
        this.stareLot = StareLot.Returnat;
    }

    void anuleaza() {
        if (!esteInainteDeReceptie()) {
            throw new IllegalStateException("Cannot cancel a lot that has already been received. Current: " + stareLot);
        }
        this.stareLot = StareLot.Anulat;
    }

    boolean esteInainteDeReceptie() {
        return this.stareLot == StareLot.Planificat || this.stareLot == StareLot.Expediat;
    }

    public IdLot getIdLot() {
        return idLot;
    }

    public String getCodLot() {
        return codLot;
    }

    public CodMaterial getMaterial() {
        return material;
    }

    public Cantitate getCantitate() {
        return cantitate;
    }

    public Cantitate getCantitatePrimita() {
        return cantitatePrimita;
    }

    public LocalDate getDataProductie() {
        return dataProductie;
    }

    public LocalDate getDataExpirare() {
        return dataExpirare;
    }

    public StareLot getStareLot() {
        return stareLot;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Lot lot)) return false;
        return getIdLot() != null && Objects.equals(getIdLot(), lot.getIdLot());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "Lot[id=" + idLot + ", cod=" + codLot + ", stare=" + stareLot + ", material=" + material + "]";
    }
}
