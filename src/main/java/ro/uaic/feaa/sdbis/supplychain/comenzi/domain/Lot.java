package ro.uaic.feaa.sdbis.supplychain.comenzi.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import ro.uaic.feaa.sdbis.supplychain.shared.CodMaterial;
import ro.uaic.feaa.sdbis.supplychain.shared.IdLot;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

/**
 * Entitate componenta a agregatului ComandaAprovizionare.
 * Metodele de mutatie sunt exclusiv package-private (~) conform Tabelelor 2.1 din Project_OOAD.docx.
 */
@Entity
@Table(name = "comenzi_loturi")
public class Lot implements Serializable {

    @NotNull
    @EmbeddedId
    private IdLot idLot;

    @NotBlank
    @Size(min = 1, max = 50)
    @Column(nullable = false)
    private String codLot;

    @NotNull
    @Embedded
    @AttributeOverride(name = "cod", column = @Column(name = "material_cod"))
    private CodMaterial material;

    @NotNull
    @Embedded
    @AttributeOverride(name = "valoare", column = @Column(name = "cantitate_valoare"))
    @AttributeOverride(name = "unitateMasura", column = @Column(name = "cantitate_um"))
    private Cantitate cantitate;

    @NotNull
    @Embedded
    @AttributeOverride(name = "valoare", column = @Column(name = "cantitate_primita_valoare"))
    @AttributeOverride(name = "unitateMasura", column = @Column(name = "cantitate_primita_um"))
    private Cantitate cantitatePrimita;

    private LocalDate dataProductie;
    private LocalDate dataExpirare;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
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
}
