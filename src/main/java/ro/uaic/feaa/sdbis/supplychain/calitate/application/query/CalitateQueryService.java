package ro.uaic.feaa.sdbis.supplychain.calitate.application.query;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ro.uaic.feaa.sdbis.supplychain.calitate.domain.Calificativ;
import ro.uaic.feaa.sdbis.supplychain.calitate.domain.Email;
import ro.uaic.feaa.sdbis.supplychain.calitate.domain.Furnizor;
import ro.uaic.feaa.sdbis.supplychain.calitate.domain.FurnizorRepo;
import ro.uaic.feaa.sdbis.supplychain.calitate.domain.InspectieCalitate;
import ro.uaic.feaa.sdbis.supplychain.calitate.domain.InspectieRepo;
import ro.uaic.feaa.sdbis.supplychain.calitate.domain.InspectorCalitate;
import ro.uaic.feaa.sdbis.supplychain.calitate.domain.InspectorRepo;
import ro.uaic.feaa.sdbis.supplychain.calitate.domain.NCRRepo;
import ro.uaic.feaa.sdbis.supplychain.calitate.domain.RaportNeconformitate;
import ro.uaic.feaa.sdbis.supplychain.calitate.domain.ScorecardLunar;
import ro.uaic.feaa.sdbis.supplychain.calitate.domain.StareFurnizor;
import ro.uaic.feaa.sdbis.supplychain.calitate.domain.StareInspectie;
import ro.uaic.feaa.sdbis.supplychain.calitate.domain.StareNCR;
import ro.uaic.feaa.sdbis.supplychain.calitate.domain.Verdict;
import ro.uaic.feaa.sdbis.supplychain.calitate.domain.computation.CalitateDashboardView;
import ro.uaic.feaa.sdbis.supplychain.calitate.domain.computation.ICalculScorecardService;
import ro.uaic.feaa.sdbis.supplychain.shared.IdFurnizor;
import ro.uaic.feaa.sdbis.supplychain.shared.annotation.QueryService;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@QueryService
public class CalitateQueryService {

    private final FurnizorRepo furnizorRepo;
    private final NCRRepo ncrRepo;
    private final InspectieRepo inspectieRepo;
    private final InspectorRepo inspectorRepo;
    private final ICalculScorecardService calculScorecardService;

    public CalitateQueryService(FurnizorRepo furnizorRepo,
                                NCRRepo ncrRepo,
                                InspectieRepo inspectieRepo,
                                InspectorRepo inspectorRepo,
                                ICalculScorecardService calculScorecardService) {
        this.furnizorRepo = furnizorRepo;
        this.ncrRepo = ncrRepo;
        this.inspectieRepo = inspectieRepo;
        this.inspectorRepo = inspectorRepo;
        this.calculScorecardService = calculScorecardService;
    }

    public CalitateDashboardView obtineDashboardCalitate() {
        // FIX JPA-02: Calcule agregate la nivel de interogari SQL in repository (fara incarcare in memorie findAll)
        int totalFurnizori = (int) furnizorRepo.countTotal();
        int furnizoriAprobati = (int) furnizorRepo.countByStare(StareFurnizor.Aprobat);
        int furnizoriBlocati = (int) furnizorRepo.countByStare(StareFurnizor.Blocat);

        int totalInspectii = (int) inspectieRepo.countTotal();
        int inspectiiAdmise = (int) inspectieRepo.countByVerdict(ro.uaic.feaa.sdbis.supplychain.calitate.domain.Verdict.Conforma);
        int inspectiiRespinse = (int) inspectieRepo.countByVerdict(ro.uaic.feaa.sdbis.supplychain.calitate.domain.Verdict.Neconforma);

        double rataConformitate = 100.0;
        if (totalInspectii > 0) {
            rataConformitate = BigDecimal.valueOf(inspectiiAdmise)
                    .divide(BigDecimal.valueOf(totalInspectii), 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100))
                    .setScale(2, RoundingMode.HALF_UP)
                    .doubleValue();
        }

        return new CalitateDashboardView(
                totalFurnizori,
                furnizoriAprobati,
                furnizoriBlocati,
                totalInspectii,
                inspectiiAdmise,
                inspectiiRespinse,
                rataConformitate
        );
    }

    public List<MonitorizareFurnizoriView> obtineMonitorizareFurnizori() {
        return obtineMonitorizareFurnizori(0, 100);
    }

    public List<MonitorizareFurnizoriView> obtineMonitorizareFurnizori(int page, int size) {
        List<Furnizor> furnizori = furnizorRepo.findFurnizori(null, null, null, page, size);
        if (furnizori.isEmpty()) {
            return Collections.emptyList();
        }

        List<IdFurnizor> fIds = furnizori.stream().map(Furnizor::getIdFurnizor).toList();
        List<RaportNeconformitate> allNcrs = ncrRepo.findByFurnizorIds(fIds);
        List<InspectieCalitate> allInspectii = inspectieRepo.findByFurnizorIds(fIds);
        Map<IdFurnizor, List<RaportNeconformitate>> ncrsByFurnizor =
            allNcrs.stream().collect(Collectors.groupingBy(RaportNeconformitate::getFurnizorId));
        Map<IdFurnizor, List<InspectieCalitate>> inspectiiByFurnizor =
            allInspectii.stream().collect(Collectors.groupingBy(InspectieCalitate::getFurnizorId));

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

            List<InspectieCalitate> fInspectii = inspectiiByFurnizor.getOrDefault(f.getIdFurnizor(), Collections.emptyList());
            long respingeri = fInspectii.stream().filter(i -> i.getVerdict() == Verdict.Neconforma).count();
            BigDecimal rataRespingere = fInspectii.isEmpty()
                    ? BigDecimal.ZERO
                    : BigDecimal.valueOf(respingeri)
                        .multiply(BigDecimal.valueOf(100))
                        .divide(BigDecimal.valueOf(fInspectii.size()), 2, RoundingMode.HALF_UP);

            views.add(new MonitorizareFurnizoriView(
                f.getIdFurnizor().getValue(),
                f.getCodFiscal(),
                f.getDenumire(),
                f.getStareFurnizor(),
                ultimulScor,
                ultimulCalificativ,
                f.calculeazaLuniConsecutiveBSauMaiBun(),
                rataRespingere,
                ncrDeschise,
                ncrEscaladate,
                istoric
            ));
        }
        return views;
    }

    public List<FurnizorListItemView> listeazaFurnizori(String stare, String search, int page, int size) {
        StareFurnizor st = stare != null && !stare.isBlank() ? StareFurnizor.valueOf(stare.trim()) : null;
        List<Furnizor> toti = furnizorRepo.findFurnizori(st, null, search, page, size);
        return toti.stream()
            .map(f -> {
                BigDecimal scor = BigDecimal.ZERO;
                Calificativ cal = Calificativ.B;
                if (!f.getScorecarduri().isEmpty()) {
                    ScorecardLunar sc = f.getScorecarduri().get(f.getScorecarduri().size() - 1);
                    scor = sc.getScor().getValoare();
                    cal = sc.calificativ();
                }
                String loc = f.getAdresa() != null ? f.getAdresa().getLocalitate() : null;
                String iban = f.getIban() != null ? f.getIban().getValoare() : null;
                Integer termen = f.getConditii() != null ? f.getConditii().getTermenPlataZile() : null;
                String mon = f.getConditii() != null && f.getConditii().getMoneda() != null ? f.getConditii().getMoneda().getCurrencyCode() : null;
                return new FurnizorListItemView(
                    f.getIdFurnizor().getValue(),
                    f.getCodFiscal(),
                    f.getDenumire(),
                    loc,
                    iban,
                    termen,
                    mon,
                    f.getStareFurnizor(),
                    cal,
                    scor,
                    f.getCategoriiMateriale()
                );
            })
            .collect(Collectors.toList());
    }

    public Optional<FurnizorDetaliiView> obtineDetaliiFurnizor(IdFurnizor id) {
        return furnizorRepo.findById(id).map(f -> {
            BigDecimal scor = BigDecimal.ZERO;
            Calificativ cal = Calificativ.B;
            List<IstoricCalificativItemView> istoric = new ArrayList<>();
            if (!f.getScorecarduri().isEmpty()) {
                ScorecardLunar sc = f.getScorecarduri().get(f.getScorecarduri().size() - 1);
                scor = sc.getScor().getValoare();
                cal = sc.calificativ();
                for (ScorecardLunar s : f.getScorecarduri()) {
                    istoric.add(new IstoricCalificativItemView(s.getLuna(), s.getScor().getValoare(), s.calificativ()));
                }
            }

            Set<String> emailuriStr = f.getEmailuri().stream().map(Email::getEmail).collect(Collectors.toSet());
            String strada = f.getAdresa() != null ? f.getAdresa().getStrada() : null;
            String numar = f.getAdresa() != null ? f.getAdresa().getNumar() : null;
            String localitate = f.getAdresa() != null ? f.getAdresa().getLocalitate() : null;
            String codPostal = f.getAdresa() != null ? f.getAdresa().getCodPostal() : null;
            String iban = f.getIban() != null ? f.getIban().getValoare() : null;
            Integer termen = f.getConditii() != null ? f.getConditii().getTermenPlataZile() : null;
            String mon = f.getConditii() != null && f.getConditii().getMoneda() != null ? f.getConditii().getMoneda().getCurrencyCode() : null;

            Map<String, BigDecimal> preturi = new HashMap<>();
            if (f.getConditii() != null && f.getConditii().getPreturiNegociate() != null) {
                f.getConditii().getPreturiNegociate().forEach((cod, val) -> preturi.put(cod.getCod(), val));
            }

            return new FurnizorDetaliiView(
                f.getIdFurnizor().getValue(),
                f.getCodFiscal(),
                f.getDenumire(),
                strada,
                numar,
                localitate,
                codPostal,
                emailuriStr,
                iban,
                termen,
                mon,
                f.getStareFurnizor(),
                f.getInspectiiNeconformeConsecutive(),
                cal,
                scor,
                f.getCategoriiMateriale(),
                preturi,
                istoric
            );
        });
    }

    public List<InspectieListItemView> listeazaInspectii(UUID furnizorId, UUID inspectorId, String stare, int page, int size) {
        IdFurnizor fId = furnizorId != null ? IdFurnizor.of(furnizorId) : null;
        StareInspectie st = stare != null && !stare.isBlank() ? StareInspectie.valueOf(stare.trim()) : null;
        List<InspectieCalitate> inspectii = inspectieRepo.findInspectii(fId, inspectorId, st, page, size);
        return inspectii.stream()
            .map(i -> new InspectieListItemView(
                i.getIdInspectie(),
                i.getLotId().getValue(),
                i.getComandaId().getValue(),
                i.getFurnizorId().getValue(),
                i.getMaterial().getCod(),
                i.getPerioadaPlanificata().getDataInceput(),
                i.getPerioadaPlanificata().getDataFinalizare(),
                i.getInspectorId(),
                i.getStareInspectie(),
                i.getVerdict(),
                i.getPlan() != null ? i.getPlan().getNivelAQL() : null,
                i.getPlan() != null ? i.getPlan().getDimensiuneEsantion() : null,
                i.getPlan() != null ? i.getPlan().getNumarAcceptare() : null,
                i.getPlan() != null ? i.getPlan().getNumarRespingere() : null,
                i.getDefecteGasite()
            ))
            .collect(Collectors.toList());
    }

    public Optional<InspectieListItemView> obtineDetaliiInspectie(UUID id) {
        return inspectieRepo.findById(id).map(i -> new InspectieListItemView(
            i.getIdInspectie(),
            i.getLotId().getValue(),
            i.getComandaId().getValue(),
            i.getFurnizorId().getValue(),
            i.getMaterial().getCod(),
            i.getPerioadaPlanificata().getDataInceput(),
            i.getPerioadaPlanificata().getDataFinalizare(),
            i.getInspectorId(),
            i.getStareInspectie(),
            i.getVerdict(),
            i.getPlan() != null ? i.getPlan().getNivelAQL() : null,
            i.getPlan() != null ? i.getPlan().getDimensiuneEsantion() : null,
            i.getPlan() != null ? i.getPlan().getNumarAcceptare() : null,
            i.getPlan() != null ? i.getPlan().getNumarRespingere() : null,
            i.getDefecteGasite()
        ));
    }

    public List<InspectorListItemView> listeazaInspectori() {
        List<InspectorCalitate> inspectori = inspectorRepo.findAllWithDetails();
        return inspectori.stream()
            .map(ins -> {
                String nume = ins.getFullName() != null ? ins.getFullName().getFirstName() + " " + ins.getFullName().getLastName() : "Inspector";
                var certs = ins.getCertificari().stream()
                    .map(c -> new InspectorListItemView.CertificareView(c.getDenumire(), c.getDataExpirare()))
                    .toList();
                var disp = ins.getDisponibilitate().stream()
                    .map(d -> new InspectorListItemView.DisponibilitateView(d.getDataInceput(), d.getDataFinalizare()))
                    .toList();
                return new InspectorListItemView(ins.getIdInspector(), nume, certs, disp);
            })
            .collect(Collectors.toList());
    }

    public List<NCRListItemView> listeazaNCR(UUID furnizorId, String stare, int page, int size) {
        IdFurnizor fId = furnizorId != null ? IdFurnizor.of(furnizorId) : null;
        StareNCR st = stare != null && !stare.isBlank() ? StareNCR.valueOf(stare.trim()) : null;
        List<RaportNeconformitate> ncrs = ncrRepo.findNCR(fId, st, page, size);
        return ncrs.stream()
            .map(n -> new NCRListItemView(
                n.getIdNCR(),
                n.getInspectieId(),
                n.getFurnizorId().getValue(),
                n.getLotId().getValue(),
                n.getCauzaRadacina(),
                n.getActiuneCorectiva() != null ? n.getActiuneCorectiva().getDescriere() : null,
                n.getActiuneCorectiva() != null ? n.getActiuneCorectiva().getResponsabil() : null,
                n.getActiuneCorectiva() != null ? n.getActiuneCorectiva().getTermen() : null,
                n.getStareNCR()
            ))
            .collect(Collectors.toList());
    }

    public Optional<NCRListItemView> obtineDetaliiNCR(UUID id) {
        return ncrRepo.findById(id).map(n -> new NCRListItemView(
            n.getIdNCR(),
            n.getInspectieId(),
            n.getFurnizorId().getValue(),
            n.getLotId().getValue(),
            n.getCauzaRadacina(),
            n.getActiuneCorectiva() != null ? n.getActiuneCorectiva().getDescriere() : null,
            n.getActiuneCorectiva() != null ? n.getActiuneCorectiva().getResponsabil() : null,
            n.getActiuneCorectiva() != null ? n.getActiuneCorectiva().getTermen() : null,
            n.getStareNCR()
        ));
    }
}
