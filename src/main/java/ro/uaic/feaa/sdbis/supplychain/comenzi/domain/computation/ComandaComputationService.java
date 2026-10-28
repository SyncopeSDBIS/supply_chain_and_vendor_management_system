package ro.uaic.feaa.sdbis.supplychain.comenzi.domain.computation;

import ro.uaic.feaa.sdbis.supplychain.comenzi.domain.ComandaAprovizionare;
import ro.uaic.feaa.sdbis.supplychain.comenzi.domain.LinieComanda;
import ro.uaic.feaa.sdbis.supplychain.shared.annotation.DomainComputationService;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

/**
 * Implementare a Serviciului de Calcul al Domeniului Comenzi (Sprint 2 - ADR-003).
 * Respecta izolarea pura de domeniu (ADR-011).
 */
@DomainComputationService
public class ComandaComputationService implements IComandaComputationService {

    @Override
    public BigDecimal calculSubtotal(ComandaAprovizionare comanda) {
        Objects.requireNonNull(comanda, "Comanda nu poate fi null!");
        BigDecimal subtotal = BigDecimal.ZERO;
        if (comanda.getLinii() != null) {
            for (LinieComanda linie : comanda.getLinii()) {
                subtotal = subtotal.add(linie.valoareLinie());
            }
        }
        return subtotal.setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public BigDecimal calculTVA(ComandaAprovizionare comanda, BigDecimal cotaTVA) {
        Objects.requireNonNull(cotaTVA, "Cota TVA nu poate fi null!");
        BigDecimal subtotal = calculSubtotal(comanda);
        return subtotal.multiply(cotaTVA).setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public BigDecimal calculTotalCuTVA(ComandaAprovizionare comanda, BigDecimal cotaTVA) {
        BigDecimal subtotal = calculSubtotal(comanda);
        BigDecimal tva = calculTVA(comanda, cotaTVA);
        return subtotal.add(tva).setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public double calculProcentLivrare(ComandaAprovizionare comanda) {
        Objects.requireNonNull(comanda, "Comanda nu poate fi null!");
        if (comanda.getLinii() == null || comanda.getLinii().isEmpty()) {
            return 0.0;
        }

        BigDecimal totalComandat = BigDecimal.ZERO;
        BigDecimal totalPrimit = BigDecimal.ZERO;

        for (LinieComanda linie : comanda.getLinii()) {
            BigDecimal qtyComandat = linie.getCantitate().getValoare();
            BigDecimal qtyPrimit = comanda.calculeazaCantitatePrimita(linie.getMaterial()).getValoare();

            totalComandat = totalComandat.add(qtyComandat);
            totalPrimit = totalPrimit.add(qtyPrimit);
        }

        if (totalComandat.compareTo(BigDecimal.ZERO) == 0) {
            return 100.0;
        }

        BigDecimal procent = totalPrimit.divide(totalComandat, 4, RoundingMode.HALF_UP)
            .multiply(BigDecimal.valueOf(100));

        if (procent.compareTo(BigDecimal.valueOf(100)) > 0) {
            procent = BigDecimal.valueOf(100);
        }
        return procent.setScale(2, RoundingMode.HALF_UP).doubleValue();
    }

    @Override
    public ComandaSummaryView genereazaSumar(ComandaAprovizionare comanda, BigDecimal cotaTVA) {
        Objects.requireNonNull(comanda, "Comanda nu poate fi null!");
        BigDecimal subtotal = calculSubtotal(comanda);
        BigDecimal tva = calculTVA(comanda, cotaTVA != null ? cotaTVA : new BigDecimal("0.19"));
        BigDecimal total = subtotal.add(tva);
        double procentLivrare = calculProcentLivrare(comanda);
        int numarLinii = comanda.getLinii() != null ? comanda.getLinii().size() : 0;
        boolean esteLivrata = comanda.esteAcoperitaIntegral();

        return new ComandaSummaryView(
            comanda.getId(),
            comanda.getNumarComanda(),
            subtotal,
            tva,
            total,
            procentLivrare,
            numarLinii,
            esteLivrata
        );
    }
}
