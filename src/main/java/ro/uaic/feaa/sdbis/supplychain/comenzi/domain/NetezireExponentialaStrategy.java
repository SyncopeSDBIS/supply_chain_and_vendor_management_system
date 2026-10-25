package ro.uaic.feaa.sdbis.supplychain.comenzi.domain;

import java.util.List;

public class NetezireExponentialaStrategy implements IMetodaEstimareLeadTime {
    private final double alpha;

    public NetezireExponentialaStrategy() {
        this(0.3);
    }

    public NetezireExponentialaStrategy(double alpha) {
        this.alpha = alpha;
    }

    @Override
    public int estimeazaZile(IstoricLivrariVO istoric) {
        List<Integer> zile = istoric.leadTimeZile();
        if (zile == null || zile.isEmpty()) return 14;
        double s = zile.get(0);
        for (int i = 1; i < zile.size(); i++) {
            s = alpha * zile.get(i) + (1.0 - alpha) * s;
        }
        return (int) Math.round(s);
    }
}
