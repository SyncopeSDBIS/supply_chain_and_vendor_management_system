package ro.uaic.feaa.sdbis.supplychain.comenzi.domain;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Domain Service pentru calculul datei de livrare folosind Strategy Pattern (MedieMobila, NetezireExponentiala, Percentila90).
 * Pure Domain: fara adnotari de Spring!
 */
public class EstimareLeadTimeService {

    private IMetodaEstimareLeadTime strategie;

    public EstimareLeadTimeService() {
        this.strategie = new MedieMobilaStrategy();
    }

    public EstimareLeadTimeService(IMetodaEstimareLeadTime strategie) {
        this.strategie = Objects.requireNonNull(strategie, "Strategia cannot be null");
    }

    public void setStrategie(IMetodaEstimareLeadTime s) {
        this.strategie = Objects.requireNonNull(s, "Strategia cannot be null");
    }

    public EstimareLivrare calculeazaDataLivrare(MetriciLivrareVO metrici, IstoricLivrariVO istoric) {
        Objects.requireNonNull(metrici, "metrici cannot be null");
        Objects.requireNonNull(istoric, "istoric cannot be null");
        int zile = strategie.estimeazaZile(istoric);
        LocalDate dataBaza = metrici.dataLivrarePromisa() != null ? metrici.dataLivrarePromisa() : metrici.dataComanda();
        LocalDate dataEstimata = dataBaza.plusDays(zile);
        return new EstimareLivrare(dataEstimata, strategie.getClass().getSimpleName());
    }
}
