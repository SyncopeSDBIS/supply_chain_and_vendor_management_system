package ro.uaic.feaa.sdbis.supplychain.comenzi.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import ro.uaic.feaa.sdbis.supplychain.comenzi.domain.event.ComandaAnulataEvent;
import ro.uaic.feaa.sdbis.supplychain.comenzi.domain.event.ComandaReceptionataEvent;
import ro.uaic.feaa.sdbis.supplychain.shared.CodMaterial;
import ro.uaic.feaa.sdbis.supplychain.shared.IdComanda;
import ro.uaic.feaa.sdbis.supplychain.shared.IdFurnizor;
import ro.uaic.feaa.sdbis.supplychain.shared.IdLot;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

/**
 * Aggregate Root pentru Bounded Context-ul Procurement Order Management.
 * Garanteaza invarianții din Tabelul 2.1 (Lifecycle Guard, Lot Quantity Boundary, Cascading Cancellation).
 */
@Entity
@Table(name = "comenzi_aprovizionare")
public class ComandaAprovizionare implements Serializable {

    @EmbeddedId
    private IdComanda id;

    @NotBlank
    @Column(nullable = false, unique = true)
    private String numarComanda;

    @NotNull
    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "furnizor_id"))
    private IdFurnizor furnizorId;

    @NotNull
    @Column(nullable = false)
    private UUID cumparatorId;

    @NotNull
    @Column(nullable = false)
    private LocalDate dataComanda;

    @NotNull
    @Column(nullable = false)
    private LocalDate dataLivrareCeruta;

    private LocalDate dataLivrarePromisa;

    @NotBlank
    @Column(nullable = false)
    private String depozit;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StareComanda stareComanda;

    @NotEmpty
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "comenzi_linii", joinColumns = @JoinColumn(name = "comanda_id"))
    private List<LinieComanda> linii = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "comanda_id")
    private List<Lot> loturi = new ArrayList<>();

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "comenzi_expedieri", joinColumns = @JoinColumn(name = "comanda_id"))
    private List<Expediere> expedieri = new ArrayList<>();

    @Embedded
    @AttributeOverride(name = "dataEstimata", column = @Column(name = "estimare_data"))
    @AttributeOverride(name = "metoda", column = @Column(name = "estimare_metoda"))
    private EstimareLivrare estimare;

    protected ComandaAprovizionare() {}

    public ComandaAprovizionare(IdComanda id, String numarComanda, IdFurnizor furnizorId, UUID cumparatorId,
                               LocalDate dataComanda, LocalDate dataLivrareCeruta, String depozit,
                               List<LinieComanda> linii) {
        this.id = Objects.requireNonNull(id, "id cannot be null");
        this.numarComanda = Objects.requireNonNull(numarComanda, "numarComanda cannot be null");
        this.furnizorId = Objects.requireNonNull(furnizorId, "furnizorId cannot be null");
        this.cumparatorId = Objects.requireNonNull(cumparatorId, "cumparatorId cannot be null");
        this.dataComanda = Objects.requireNonNull(dataComanda, "dataComanda cannot be null");
        this.dataLivrareCeruta = Objects.requireNonNull(dataLivrareCeruta, "dataLivrareCeruta cannot be null");
        this.depozit = Objects.requireNonNull(depozit, "depozit cannot be null");
        if (linii == null || linii.isEmpty()) {
            throw new IllegalArgumentException("Comanda must have at least one line");
        }
        this.linii.addAll(linii);
        this.stareComanda = StareComanda.Solicitat;
    }

    public void trimiteLaFurnizor() {
        validaStarePermiteOperatia(StareComanda.Solicitat);
    }

    public void actualizeazaDetalii(LocalDate dataLivrareCeruta, String depozit) {
        validaStarePermiteOperatia(StareComanda.Solicitat);
        if (dataLivrareCeruta != null) {
            if (dataLivrareCeruta.isBefore(this.dataComanda)) {
                throw new IllegalArgumentException("Data livrare ceruta cannot be before data comanda");
            }
            this.dataLivrareCeruta = dataLivrareCeruta;
        }
        if (depozit != null && !depozit.trim().isEmpty()) {
            this.depozit = depozit.trim();
        }
    }

    public void confirma(LocalDate dataPromisa) {
        validaStarePermiteOperatia(StareComanda.Solicitat);
        this.dataLivrarePromisa = Objects.requireNonNull(dataPromisa, "dataLivrarePromisa cannot be null");
        this.stareComanda = StareComanda.Confirmat;
    }

    public void refuza() {
        validaStarePermiteOperatia(StareComanda.Solicitat);
        this.stareComanda = StareComanda.Refuzat;
    }

    public IdLot planificaLot(String codLot, CodMaterial material, Cantitate cantitate, LocalDate productie, LocalDate expirare) {
        validaStarePermiteOperatia(StareComanda.Confirmat, StareComanda.InTranzit, StareComanda.ReceptionatPartial);
        validaCantitateLot(material, cantitate);
        IdLot newId = IdLot.random();
        Lot lot = new Lot(newId, codLot, material, cantitate, productie, expirare);
        this.loturi.add(lot);
        return newId;
    }

    public void inregistreazaExpediere(String transportator, String awb, Set<IdLot> loturiExpediate) {
        validaStarePermiteOperatia(StareComanda.Confirmat, StareComanda.InTranzit, StareComanda.ReceptionatPartial);
        for (IdLot lotId : loturiExpediate) {
            Lot lot = gasesteLot(lotId);
            lot.expediaza();
        }
        this.expedieri.add(new Expediere(transportator, awb, LocalDate.now(), loturiExpediate));
        this.stareComanda = StareComanda.InTranzit;
    }

    public ComandaReceptionataEvent inregistreazaReceptie(IdLot lotId, Cantitate primit) {
        validaStarePermiteOperatia(StareComanda.InTranzit, StareComanda.ReceptionatPartial);
        Lot lot = gasesteLot(lotId);
        lot.receptioneaza(primit);

        boolean acoperita = esteAcoperitaIntegral();
        if (acoperita) {
            this.stareComanda = StareComanda.Receptionat;
        } else {
            this.stareComanda = StareComanda.ReceptionatPartial;
        }

        return new ComandaReceptionataEvent(
            UUID.randomUUID(),
            java.time.Instant.now(),
            this.id,
            lotId,
            lot.getMaterial(),
            primit,
            acoperita
        );
    }

    public void plaseazaLotInCarantina(IdLot lotId) {
        Lot lot = gasesteLot(lotId);
        lot.plaseazaInCarantina();
    }

    public void aplicaVerdictInspectie(IdLot lotId, RezultatCalitateLot rezultat) {
        Lot lot = gasesteLot(lotId);
        if (rezultat == RezultatCalitateLot.Conform || rezultat == RezultatCalitateLot.Scutit) {
            lot.elibereazaInStoc();
        } else {
            lot.respinge();
        }
    }

    public void returneazaLotFurnizorului(IdLot lotId) {
        Lot lot = gasesteLot(lotId);
        lot.returneazaFurnizorului();
    }

    public ComandaAnulataEvent anuleazaComanda() {
        if (this.stareComanda == StareComanda.Receptionat || this.stareComanda == StareComanda.Anulat) {
            throw new IllegalStateException("Cannot cancel an order in state: " + stareComanda);
        }

        List<IdLot> loturiAnulate = new ArrayList<>();
        for (Lot lot : this.loturi) {
            if (lot.esteInainteDeReceptie()) {
                lot.anuleaza();
                loturiAnulate.add(lot.getIdLot());
            }
        }
        this.stareComanda = StareComanda.Anulat;

        return new ComandaAnulataEvent(
            UUID.randomUUID(),
            java.time.Instant.now(),
            this.id,
            loturiAnulate
        );
    }

    public Cantitate calculeazaCantitatePrimita(CodMaterial material) {
        Cantitate total = null;
        for (Lot lot : loturi) {
            if (lot.getMaterial().equals(material)) {
                if (total == null) {
                    total = lot.getCantitatePrimita();
                } else {
                    total = total.adauga(lot.getCantitatePrimita());
                }
            }
        }
        if (total == null) {
            for (LinieComanda l : linii) {
                if (l.getMaterial().equals(material)) {
                    return Cantitate.zero(l.getCantitate().getUnitateMasura());
                }
            }
            return Cantitate.zero("BUC");
        }
        return total;
    }

    public Cantitate calculeazaCantitateRamasa(CodMaterial material) {
        LinieComanda linie = linii.stream()
            .filter(l -> l.getMaterial().equals(material))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Material not found on order: " + material));
        Cantitate primita = calculeazaCantitatePrimita(material);
        BigDecimal ramasa = linie.getCantitate().getValoare().subtract(primita.getValoare());
        if (ramasa.compareTo(BigDecimal.ZERO) < 0) ramasa = BigDecimal.ZERO;
        return new Cantitate(ramasa, linie.getCantitate().getUnitateMasura());
    }

    public boolean esteAcoperitaIntegral() {
        for (LinieComanda linie : linii) {
            Cantitate primita = calculeazaCantitatePrimita(linie.getMaterial());
            if (!primita.acopera(linie.getCantitate())) {
                return false;
            }
        }
        return true;
    }

    public BigDecimal calculeazaTotal() {
        BigDecimal total = BigDecimal.ZERO;
        if (linii != null) {
            for (LinieComanda l : linii) {
                total = total.add(l.valoareLinie());
            }
        }
        return total;
    }

    public MetriciLivrareVO extrageMetrici() {
        CodMaterial primMaterial = linii.get(0).getMaterial();
        return new MetriciLivrareVO(furnizorId, primMaterial, dataComanda, dataLivrarePromisa);
    }

    public void inregistreazaEstimare(EstimareLivrare e) {
        this.estimare = Objects.requireNonNull(e, "EstimareLivrare cannot be null");
    }

    // Invarianti Privati (Tabelul 2.1)
    private void validaStarePermiteOperatia(StareComanda... permise) {
        for (StareComanda p : permise) {
            if (this.stareComanda == p) return;
        }
        throw new IllegalStateException("Operation not allowed in current state: " + stareComanda);
    }

    private void validaCantitateLot(CodMaterial material, Cantitate nouaCantitate) {
        LinieComanda linie = linii.stream()
            .filter(l -> l.getMaterial().equals(material))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Material not on order: " + material));

        BigDecimal planificatTotal = BigDecimal.ZERO;
        for (Lot lot : loturi) {
            if (lot.getMaterial().equals(material) && lot.getStareLot() != StareLot.Anulat) {
                planificatTotal = planificatTotal.add(lot.getCantitate().getValoare());
            }
        }
        BigDecimal dupaAdaugare = planificatTotal.add(nouaCantitate.getValoare());
        if (dupaAdaugare.compareTo(linie.getCantitate().getValoare()) > 0) {
            throw new IllegalArgumentException("Sum of lot quantities (" + dupaAdaugare + ") exceeds ordered quantity (" + linie.getCantitate().getValoare() + ")");
        }
    }

    private Lot gasesteLot(IdLot lotId) {
        return loturi.stream()
            .filter(l -> l.getIdLot().equals(lotId))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Lot not found on this order: " + lotId));
    }

    public IdComanda getId() { return id; }
    public String getNumarComanda() { return numarComanda; }
    public IdFurnizor getFurnizorId() { return furnizorId; }
    public UUID getCumparatorId() { return cumparatorId; }
    public LocalDate getDataComanda() { return dataComanda; }
    public LocalDate getDataLivrareCeruta() { return dataLivrareCeruta; }
    public LocalDate getDataLivrarePromisa() { return dataLivrarePromisa; }
    public String getDepozit() { return depozit; }
    public StareComanda getStareComanda() { return stareComanda; }
    public List<LinieComanda> getLinii() { return Collections.unmodifiableList(linii); }
    public List<Lot> getLoturi() { return Collections.unmodifiableList(loturi); }
    public List<Expediere> getExpedieri() { return Collections.unmodifiableList(expedieri); }
    public EstimareLivrare getEstimare() { return estimare; }
}
