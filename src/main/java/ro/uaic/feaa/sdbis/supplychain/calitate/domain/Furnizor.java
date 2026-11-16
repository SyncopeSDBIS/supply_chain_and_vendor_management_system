package ro.uaic.feaa.sdbis.supplychain.calitate.domain;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.CascadeType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embedded;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderColumn;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import ro.uaic.feaa.sdbis.supplychain.calitate.domain.event.FurnizorBlocatEvent;
import ro.uaic.feaa.sdbis.supplychain.calitate.domain.event.FurnizorSuspendatEvent;
import ro.uaic.feaa.sdbis.supplychain.shared.IdFurnizor;
import ro.uaic.feaa.sdbis.supplychain.shared.Perioada;
import ro.uaic.feaa.sdbis.supplychain.shared.annotation.AggregateRoot;

import java.io.Serializable;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@AggregateRoot
@Entity
@Table(name = "calitate_furnizori")
public class Furnizor implements Serializable {
    private static final long serialVersionUID = 1L;


    @NotNull
    @EmbeddedId
    private IdFurnizor id;

    @NotBlank
    @Size(min = 2, max = 50)
    @Column(name = "cod_fiscal", nullable = false, unique = true, length = 50)
    private String codFiscal;

    @NotBlank
    @Size(min = 2, max = 255)
    @Column(name = "denumire", nullable = false, length = 255)
    private String denumire;

    @Embedded
    private Adresa adresa;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "calitate_furnizori_emailuri", joinColumns = @JoinColumn(name = "furnizor_id"))
    @AttributeOverride(name = "email", column = @Column(name = "email", nullable = false, length = 255))
    @SuppressWarnings("serial") // Collection interface Set does not implement Serializable; runtime instance is HashSet
    private Set<Email> emailuri = new HashSet<>();

    @NotNull
    @Embedded
    @AttributeOverride(name = "valoare", column = @Column(name = "iban_valoare", length = 50))
    private Iban iban;

    @NotEmpty
    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "calitate_furnizori_categorii", joinColumns = @JoinColumn(name = "furnizor_id"))
    @Column(name = "categorie", nullable = false, length = 100)
    @SuppressWarnings("serial") // Collection interface Set does not implement Serializable directly; runtime instance is HashSet
    private Set<String> categoriiMateriale = new HashSet<>();

    @Embedded
    private ConditiiContractuale conditii;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "furnizor_preturi_negociate", joinColumns = @JoinColumn(name = "furnizor_id_val"))
    @OrderColumn(name = "pret_idx")
    @SuppressWarnings("serial") // Collection interface List does not implement Serializable directly; runtime instance is ArrayList
    private List<PretNegociat> preturiNegociate = new ArrayList<>();

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "stare_furnizor", nullable = false, length = 50)
    private StareFurnizor stareFurnizor;

    @OneToMany(mappedBy = "furnizor", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @OrderColumn(name = "scorecard_idx")
    @SuppressWarnings("serial") // Collection interface List does not implement Serializable directly; runtime instance is ArrayList
    private List<ScorecardLunar> scorecarduri = new ArrayList<>();

    @Min(0)
    @Column(name = "inspectii_neconforme_consecutive", nullable = false)
    private int inspectiiNeconformeConsecutive = 0;

    @Column(name = "created_at")
    private java.time.Instant createdAt = java.time.Instant.now();

    @Column(name = "updated_at")
    private java.time.Instant updatedAt = java.time.Instant.now();

    @Version
    @Column(name = "version", nullable = false)
    private Long version = 0L;

    @PrePersist
    protected void onPrePersist() {
        if (createdAt == null) createdAt = java.time.Instant.now();
        updatedAt = java.time.Instant.now();
    }

    @PreUpdate
    protected void onPreUpdate() {
        updatedAt = java.time.Instant.now();
    }

    protected Furnizor() {}

    public Furnizor(IdFurnizor id, String codFiscal, String denumire, Adresa adresa,
                    Set<Email> emailuri, Iban iban, Set<String> categoriiMateriale,
                    ConditiiContractuale conditii) {
        this.id = Objects.requireNonNull(id, "id cannot be null");
        this.codFiscal = Objects.requireNonNull(codFiscal, "codFiscal cannot be null");
        this.denumire = Objects.requireNonNull(denumire, "denumire cannot be null");
        this.adresa = adresa;
        if (emailuri != null) this.emailuri.addAll(emailuri);
        this.iban = iban;
        if (categoriiMateriale != null) this.categoriiMateriale.addAll(categoriiMateriale);
        this.conditii = conditii;
        if (conditii != null && conditii.getPreturiNegociate() != null) {
            conditii.getPreturiNegociate().forEach((mat, pret) -> this.preturiNegociate.add(new PretNegociat(mat, pret)));
        }
        this.stareFurnizor = StareFurnizor.Candidat;
    }

    public void actualizeazaProfil(String denumire, Adresa adresa, Set<Email> emailuri,
                                  Iban iban, Set<String> categoriiMateriale, ConditiiContractuale conditii) {
        if (denumire != null && !denumire.trim().isEmpty()) this.denumire = denumire.trim();
        if (adresa != null) this.adresa = adresa;
        if (emailuri != null) { this.emailuri.clear(); this.emailuri.addAll(emailuri); }
        if (iban != null) this.iban = iban;
        if (categoriiMateriale != null) { this.categoriiMateriale.clear(); this.categoriiMateriale.addAll(categoriiMateriale); }
        if (conditii != null) {
            this.conditii = conditii;
            this.preturiNegociate.clear();
            if (conditii.getPreturiNegociate() != null) {
                conditii.getPreturiNegociate().forEach((mat, pret) -> this.preturiNegociate.add(new PretNegociat(mat, pret)));
            }
        }
    }

    public void trimiteLaEvaluare() {
        if (stareFurnizor != StareFurnizor.Candidat) {
            throw new IllegalStateException("Only Candidat can be sent to evaluation, current: " + stareFurnizor);
        }
        this.stareFurnizor = StareFurnizor.InEvaluare;
    }

    public void aproba() {
        if (this.stareFurnizor != StareFurnizor.InEvaluare && this.stareFurnizor != StareFurnizor.Conditionat) {
            throw new IllegalStateException("Only InEvaluare or Conditionat can be approved. Current: " + stareFurnizor);
        }
        this.stareFurnizor = StareFurnizor.Aprobat;
        this.inspectiiNeconformeConsecutive = 0;
    }

    public void aplicaScorecard(ScorecardLunar s) {
        if (this.stareFurnizor == StareFurnizor.Blocat) {
            throw new IllegalStateException("Nu se pot aplica evaluari periodice unui furnizor aflat in starea Blocat.");
        }
        validaScorecardUnicPeLuna(s.getLuna());
        s.ataseazaFurnizor(this);
        scorecarduri.add(s);
        Calificativ c = s.calificativ();

        if (c == Calificativ.C && this.stareFurnizor == StareFurnizor.Aprobat) {
            this.stareFurnizor = StareFurnizor.Conditionat;
        } else if (c == Calificativ.D) {
            this.stareFurnizor = StareFurnizor.Suspendat;
        } else if (this.stareFurnizor == StareFurnizor.Conditionat && calculeazaLuniConsecutiveBSauMaiBun() >= 3) {
            this.stareFurnizor = StareFurnizor.Aprobat;
        }
    }

    public FurnizorSuspendatEvent inregistreazaRezultatInspectie(Verdict verdict) {
        if (verdict == Verdict.Conforma) {
            this.inspectiiNeconformeConsecutive = 0;
            return null;
        } else {
            this.inspectiiNeconformeConsecutive++;
            if (this.inspectiiNeconformeConsecutive >= 2 && this.stareFurnizor == StareFurnizor.Conditionat) {
                this.stareFurnizor = StareFurnizor.Suspendat;
                return new FurnizorSuspendatEvent(UUID.randomUUID(), Instant.now(), this.id, "2 inspectii neconforme consecutive in stare Conditionat");
            }
            return null;
        }
    }

    public void suspenda(String motiv) {
        if (this.stareFurnizor == StareFurnizor.Blocat) {
            throw new IllegalStateException("Un furnizor blocat nu poate fi trecut in starea Suspendat.");
        }
        this.stareFurnizor = StareFurnizor.Suspendat;
    }

    public FurnizorBlocatEvent blocheaza(String motiv) {
        this.stareFurnizor = StareFurnizor.Blocat;
        return new FurnizorBlocatEvent(UUID.randomUUID(), Instant.now(), this.id, motiv);
    }

    public boolean esteEligibilPentruComenzi() {
        return this.stareFurnizor == StareFurnizor.Aprobat || this.stareFurnizor == StareFurnizor.Conditionat;
    }

    public int calculeazaLuniConsecutiveBSauMaiBun() {
        if (scorecarduri == null || scorecarduri.isEmpty()) {
            return 0;
        }
        List<ScorecardLunar> sortate = new ArrayList<>(scorecarduri);
        sortate.sort((s1, s2) -> s1.getLuna().getDataInceput().compareTo(s2.getLuna().getDataInceput()));
        int count = 0;
        for (int i = sortate.size() - 1; i >= 0; i--) {
            Calificativ c = sortate.get(i).calificativ();
            if (c == Calificativ.A || c == Calificativ.B) {
                count++;
            } else {
                break;
            }
        }
        return count;
    }

    private void validaScorecardUnicPeLuna(Perioada luna) {
        for (ScorecardLunar s : scorecarduri) {
            if (s.getLuna().equals(luna)) {
                throw new IllegalStateException("Scorecard already exists for period: " + luna);
            }
        }
    }

    public IdFurnizor getIdFurnizor() { return id; }
    public IdFurnizor getId() { return id; }
    public String getCodFiscal() { return codFiscal; }
    public String getDenumire() { return denumire; }
    public Adresa getAdresa() { return adresa; }
    public Set<Email> getEmailuri() { return Collections.unmodifiableSet(emailuri); }
    public Iban getIban() { return iban; }
    public Set<String> getCategoriiMateriale() { return Collections.unmodifiableSet(categoriiMateriale); }
    public ConditiiContractuale getConditii() { return conditii; }
    public List<PretNegociat> getPreturiNegociate() { return Collections.unmodifiableList(preturiNegociate); }
    public StareFurnizor getStareFurnizor() { return stareFurnizor; }
    public List<ScorecardLunar> getScorecarduri() { return Collections.unmodifiableList(scorecarduri); }
    public int getInspectiiNeconformeConsecutive() { return inspectiiNeconformeConsecutive; }
    public java.time.Instant getCreatedAt() { return createdAt; }
    public java.time.Instant getUpdatedAt() { return updatedAt; }
    public Long getVersion() { return version; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Furnizor that)) return false;
        return getId() != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "Furnizor[id=" + id + ", denumire=" + denumire + ", cui=" + codFiscal + ", stare=" + stareFurnizor + "]";
    }
}
