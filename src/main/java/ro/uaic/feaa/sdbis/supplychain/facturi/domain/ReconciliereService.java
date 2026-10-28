package ro.uaic.feaa.sdbis.supplychain.facturi.domain;

import ro.uaic.feaa.sdbis.supplychain.shared.annotation.DomainService;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Domain Service pentru reconcilierea in 3 cai (Three-Way Match: Factura vs. Comanda vs. NIR).
 * Pure Domain: fara Spring.
 */
@DomainService
public class ReconciliereService {

    public RezultatReconciliere reconciliaza(FacturaFurnizor f, NIR nir, ComandaSummaryVO comanda, Toleranta t) {
        Objects.requireNonNull(f, "Factura cannot be null");
        Objects.requireNonNull(nir, "NIR cannot be null");
        Objects.requireNonNull(comanda, "Comanda cannot be null");
        Objects.requireNonNull(t, "Toleranta cannot be null");

        List<String> diferente = new ArrayList<>();

        for (LinieFactura lf : f.getLinii()) {
            // 1. Verificare cantitate facturata vs. NIR
            BigDecimal cantNIR = nir.calculeazaCantitateReceptionata(lf.getMaterial());
            if (!t.acopera(cantNIR, lf.getCantitate())) {
                diferente.add("Cantitate facturata (" + lf.getCantitate() + ") difera de cantitatea din NIR (" + cantNIR + ") pt " + lf.getMaterial());
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
