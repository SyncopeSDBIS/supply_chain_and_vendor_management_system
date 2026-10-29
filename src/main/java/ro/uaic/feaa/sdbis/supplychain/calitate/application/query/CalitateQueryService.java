package ro.uaic.feaa.sdbis.supplychain.calitate.application.query;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ro.uaic.feaa.sdbis.supplychain.calitate.domain.*;
import ro.uaic.feaa.sdbis.supplychain.shared.IdFurnizor;
import ro.uaic.feaa.sdbis.supplychain.shared.annotation.QueryService;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@QueryService
public class CalitateQueryService {

    private final FurnizorRepo furnizorRepo;
    private final NCRRepo ncrRepo;

    public CalitateQueryService(FurnizorRepo furnizorRepo, NCRRepo ncrRepo) {
        this.furnizorRepo = furnizorRepo;
        this.ncrRepo = ncrRepo;
    }

    public List<MonitorizareFurnizoriView> obtineMonitorizareFurnizori() {
        List<Furnizor> furnizori = furnizorRepo.findAll();
        List<RaportNeconformitate> allNcrs = ncrRepo.findAll();
        Map<IdFurnizor, List<RaportNeconformitate>> ncrsByFurnizor =
            allNcrs.stream().collect(Collectors.groupingBy(RaportNeconformitate::getFurnizorId));

        List<MonitorizareFurnizoriView> views = new ArrayList<>();

        for (Furnizor f : furnizori) {
            BigDecimal ultimulScor = BigDecimal.ZERO;
            Calificativ ultimulCalificativ = Calificativ.B;
            List<IstoricCalificativItemView> istoric = new ArrayList<>();

            if (!f.getScorecarduri().isEmpty()) {
                ScorecardLunar s = f.getScorecarduri().get(f.getScorecarduri().size() - 1);
                ultimulScor = s.getScor().getValoare();
                ultimulCalificativ = s.calificativ();
                for (ScorecardLunar sc : f.getScorecarduri()) {
                    istoric.add(new IstoricCalificativItemView(sc.getLuna(), sc.getScor().getValoare(), sc.calificativ()));
                }
            }

            int ncrDeschise = 0;
            int ncrEscaladate = 0;
            List<RaportNeconformitate> furnizorNcrs = ncrsByFurnizor.getOrDefault(f.getIdFurnizor(), Collections.emptyList());
            for (RaportNeconformitate ncr : furnizorNcrs) {
                if (ncr.getStareNCR() == StareNCR.Deschis || ncr.getStareNCR() == StareNCR.InAnalizaCauzei) {
                    ncrDeschise++;
                } else if (ncr.getStareNCR() == StareNCR.Escaladat) {
                    ncrEscaladate++;
                }
            }

            views.add(new MonitorizareFurnizoriView(
                f.getIdFurnizor().getValue(),
                f.getCodFiscal(),
                f.getDenumire(),
                f.getStareFurnizor(),
                ultimulScor,
                ultimulCalificativ,
                f.calculeazaLuniConsecutiveBSauMaiBun(),
                BigDecimal.valueOf(1.5),
                ncrDeschise,
                ncrEscaladate,
                istoric
            ));
        }
        return views;
    }
}
