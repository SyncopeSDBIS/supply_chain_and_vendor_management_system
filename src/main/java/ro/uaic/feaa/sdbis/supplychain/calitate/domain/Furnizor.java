package ro.uaic.feaa.sdbis.supplychain.calitate.domain;

import jakarta.persistence.*;
import ro.uaic.feaa.sdbis.supplychain.calitate.domain.event.FurnizorBlocatEvent;
import ro.uaic.feaa.sdbis.supplychain.calitate.domain.event.FurnizorSuspendatEvent;
import ro.uaic.feaa.sdbis.supplychain.shared.IdFurnizor;
import ro.uaic.feaa.sdbis.supplychain.shared.Perioada;

import java.io.Serializable;
import java.time.Instant;
import java.util.*;

@Entity
@Table(name = "calitate_furnizori")
public class Furnizor implements Serializable {

    @EmbeddedId
    private IdFurnizor id;

    @Column(nullable = false, unique = true)
    private String codFiscal;

    @Column(nullable = false)
    private String denumire;

    @Embedded
    private Adresa adresa;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "calitate_furnizori_emailuri", joinColumns = @JoinColumn(name = "furnizor_id"))
    private Set<Email> emailuri = new HashSet<>();

    @Embedded
    @AttributeOverride(name = "valoare", column = @Column(name = "iban_valoare"))
    private Iban iban;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "calitate_furnizori_categorii", joinColumns = @JoinColumn(name = "furnizor_id"))
    private Set<String> categoriiMateriale = new HashSet<>();

    @Embedded
    private ConditiiContractuale conditii;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StareFurnizor stareFurnizor;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "furnizor_id")
    private List<ScorecardLunar> scorecarduri = new ArrayList<>();

    private int inspectiiNeconformeConsecutive = 0;

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
        this.stareFurnizor = StareFurnizor.Candidat;
    }

    public void actualizeazaProfil(String denumire, Adresa adresa, Set<Email> emailuri,
                                  Iban iban, Set<String> categoriiMateriale, ConditiiContractuale conditii) {
        if (denumire != null && !denumire.trim().isEmpty()) this.denumire = denumire.trim();
        if (adresa != null) this.adresa = adresa;
        if (emailuri != null) { this.emailuri.clear(); this.emailuri.addAll(emailuri); }
        if (iban != null) this.iban = iban;
        if (categoriiMateriale != null) { this.categoriiMateriale.clear(); this.categoriiMateriale.addAll(categoriiMateriale); }
        if (conditii != null) this.conditii = conditii;
    }

    public void trimiteLaEvaluare() {
        if (stareFurnizor != StareFurnizor.Candidat) {
            throw new IllegalStateException("Only Candidat can be sent to evaluation, current: " + stareFurnizor);
        }
        this.stareFurnizor = StareFurnizor.InEvaluare;
    }

    public void aproba() {
        this.stareFurnizor = StareFurnizor.Aprobat;
        this.inspectiiNeconformeConsecutive = 0;
    }

    public void aplicaScorecard(ScorecardLunar s) {
        validaScorecardUnicPeLuna(s.getLuna());
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
        int count = 0;
        for (int i = scorecarduri.size() - 1; i >= 0; i--) {
            Calificativ c = scorecarduri.get(i).calificativ();
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
    public String getCodFiscal() { return codFiscal; }
    public String getDenumire() { return denumire; }
    public Adresa getAdresa() { return adresa; }
    public Set<Email> getEmailuri() { return Collections.unmodifiableSet(emailuri); }
    public Iban getIban() { return iban; }
    public Set<String> getCategoriiMateriale() { return Collections.unmodifiableSet(categoriiMateriale); }
    public ConditiiContractuale getConditii() { return conditii; }
    public StareFurnizor getStareFurnizor() { return stareFurnizor; }
    public List<ScorecardLunar> getScorecarduri() { return Collections.unmodifiableList(scorecarduri); }
    public int getInspectiiNeconformeConsecutive() { return inspectiiNeconformeConsecutive; }
}
