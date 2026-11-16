package ro.uaic.feaa.sdbis.supplychain.facturi.domain;

import ro.uaic.feaa.sdbis.supplychain.shared.annotation.DomainService;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Domain Service pentru reconcilierea in 3 cai (Three-Way Match: Factura vs. Comanda vs. NIR).
 * Pure Domain: fara Spring.
 * Suporta reconciliere consolidata Multi-NIR (livrari partiale multiple) si NIR individual (DDD-R01).
 */
@DomainService
public class ReconciliereService {

    /**
     * Reconciliere retrocompatibila pentru un singur NIR.
     * Deleaga catre reconcilierea consolidata multi-NIR.
     */
    public RezultatReconciliere reconciliaza(FacturaFurnizor f, NIR nir, ComandaSummaryVO comanda, Toleranta t) {
        Objects.requireNonNull(nir, "NIR cannot be null");
        return reconciliaza(f, List.of(nir), comanda, t);
    }

    /**
     * Reconciliere consolidata Multi-NIR (Three-Way Match pe livrari partiale multiple).
     * Agrega cantitatile receptionate conforme din toate NIR-urile pe fiecare linie / SKU facturat.
     */
    public RezultatReconciliere reconciliaza(FacturaFurnizor f, List<NIR> niruri, ComandaSummaryVO comanda, Toleranta t) {
        Objects.requireNonNull(f, "Factura cannot be null");
        Objects.requireNonNull(niruri, "Lista de NIR-uri cannot be null");
        if (niruri.isEmpty()) {
            throw new IllegalArgumentException("Lista de NIR-uri pentru reconciliere nu poate fi vida");
        }
        Objects.requireNonNull(comanda, "Comanda cannot be null");
        Objects.requireNonNull(t, "Toleranta cannot be null");

        List<String> diferente = new ArrayList<>();

        for (LinieFactura lf : f.getLinii()) {
            // 1. Verificare cantitate facturata vs. cantitate agregata din toate NIR-urile conforme
            BigDecimal cantNIRAgregata = BigDecimal.ZERO;
            for (NIR nir : niruri) {
                if (nir != null && nir.getStareNIR() != StareNIR.Stornat) {
                    cantNIRAgregata = cantNIRAgregata.add(nir.calculeazaCantitateReceptionata(lf.getMaterial()));
                }
            }

            if (!t.acopera(cantNIRAgregata, lf.getCantitate())) {
                diferente.add("Cantitate facturata (" + lf.getCantitate() + ") difera de cantitatea din NIR (" + cantNIRAgregata + ") pt " + lf.getMaterial());
            }

            // 2. Verificare pret si cantitate comandata
            LinieSumar ls = comanda.linii().stream()
                .filter(l -> l.material().equals(lf.getMaterial()))
                .findFirst()
                .orElse(null);

            if (ls == null) {
                diferente.add("Materialul " + lf.getMaterial() + " din factura nu exista pe comanda");
            } else {
                if (!t.acopera(ls.pretUnitar(), lf.getPretUnitar().getValoare())) {
                    diferente.add("Pretul facturat (" + lf.getPretUnitar().getValoare() + ") difera de pretul din comanda (" + ls.pretUnitar() + ") pt " + lf.getMaterial());
                }
            }
        }

        boolean aprobata = diferente.isEmpty();
        return new RezultatReconciliere(aprobata, diferente);
    }
}
