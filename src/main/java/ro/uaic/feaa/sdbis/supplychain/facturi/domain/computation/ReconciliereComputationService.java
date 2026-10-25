package ro.uaic.feaa.sdbis.supplychain.facturi.domain.computation;

import ro.uaic.feaa.sdbis.supplychain.facturi.domain.FacturaFurnizor;
import ro.uaic.feaa.sdbis.supplychain.facturi.domain.StareFactura;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Objects;

/**
 * Implementare a Serviciului de Calcul Financiar si Reconciliere Facturi (Sprint 2 - ADR-003).
 * Respecta izolarea pura de domeniu (ADR-011).
 */
public class ReconciliereComputationService implements IReconciliereComputationService {

    @Override
    public BigDecimal calculeazaRestPlataTotal(List<FacturaFurnizor> facturi) {
        if (facturi == null || facturi.isEmpty()) {
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }

        BigDecimal restTotal = BigDecimal.ZERO;
        for (FacturaFurnizor f : facturi) {
            if (f.getStareFactura() != StareFactura.Anulata && f.getStareFactura() != StareFactura.Platita) {
                restTotal = restTotal.add(f.calculeazaRestDePlata().getValoare());
            }
        }
        return restTotal.setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public PlatiSummaryView calculeazaIndicatoriPlati(List<FacturaFurnizor> facturi) {
        if (facturi == null || facturi.isEmpty()) {
            return new PlatiSummaryView(BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, 0, 0, 0);
        }

        BigDecimal totalFacturat = BigDecimal.ZERO;
        BigDecimal totalAchitat = BigDecimal.ZERO;
        BigDecimal totalRestant = BigDecimal.ZERO;
        int achitateIntegral = 0;
        int restante = 0;

        for (FacturaFurnizor f : facturi) {
            if (f.getStareFactura() == StareFactura.Anulata) {
                continue;
            }

            BigDecimal valFactura = f.getValoare().getValoareTotala();
            BigDecimal rest = f.calculeazaRestDePlata().getValoare();
            BigDecimal achitat = valFactura.subtract(rest);

            totalFacturat = totalFacturat.add(valFactura);
            totalAchitat = totalAchitat.add(achitat);
            totalRestant = totalRestant.add(rest);

            if (f.getStareFactura() == StareFactura.Platita || rest.compareTo(BigDecimal.ZERO) == 0) {
                achitateIntegral++;
            } else {
                restante++;
            }
        }

        return new PlatiSummaryView(
            totalFacturat.setScale(2, RoundingMode.HALF_UP),
            totalAchitat.setScale(2, RoundingMode.HALF_UP),
            totalRestant.setScale(2, RoundingMode.HALF_UP),
            facturi.size(),
            achitateIntegral,
            restante
        );
    }

    @Override
    public double calculeazaProcentAchitare(FacturaFurnizor factura) {
        Objects.requireNonNull(factura, "Factura nu poate fi null!");
        BigDecimal total = factura.getValoare().getValoareTotala();
        if (total.compareTo(BigDecimal.ZERO) == 0) {
            return 100.0;
        }

        BigDecimal rest = factura.calculeazaRestDePlata().getValoare();
        BigDecimal achitat = total.subtract(rest);

        BigDecimal procent = achitat.divide(total, 4, RoundingMode.HALF_UP)
            .multiply(BigDecimal.valueOf(100));

        if (procent.compareTo(BigDecimal.valueOf(100)) > 0) {
            procent = BigDecimal.valueOf(100);
        }
        return procent.setScale(2, RoundingMode.HALF_UP).doubleValue();
    }
}
