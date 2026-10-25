package ro.uaic.feaa.sdbis.supplychain.comenzi.domain;

import java.util.List;

public class MedieMobilaStrategy implements IMetodaEstimareLeadTime {
    @Override
    public int estimeazaZile(IstoricLivrariVO istoric) {
        List<Integer> zile = istoric.leadTimeZile();
        if (zile == null || zile.isEmpty()) return 14;
        double avg = zile.stream().mapToInt(Integer::intValue).average().orElse(14.0);
        return (int) Math.round(avg);
    }
}
