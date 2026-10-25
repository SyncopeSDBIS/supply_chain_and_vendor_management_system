package ro.uaic.feaa.sdbis.supplychain.comenzi.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Percentila90Strategy implements IMetodaEstimareLeadTime {
    @Override
    public int estimeazaZile(IstoricLivrariVO istoric) {
        List<Integer> zile = new ArrayList<>(istoric.leadTimeZile());
        if (zile.isEmpty()) return 14;
        Collections.sort(zile);
        int index = (int) Math.ceil(0.90 * zile.size()) - 1;
        if (index < 0) index = 0;
        return zile.get(index);
    }
}
