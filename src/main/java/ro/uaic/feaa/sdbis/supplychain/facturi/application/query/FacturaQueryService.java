package ro.uaic.feaa.sdbis.supplychain.facturi.application.query;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ro.uaic.feaa.sdbis.supplychain.facturi.domain.FacturaFurnizor;
import ro.uaic.feaa.sdbis.supplychain.facturi.domain.FacturaRepo;
import ro.uaic.feaa.sdbis.supplychain.facturi.domain.FurnizoriBlocatiRepo;
import ro.uaic.feaa.sdbis.supplychain.facturi.domain.NIR;
import ro.uaic.feaa.sdbis.supplychain.facturi.domain.NIRRepo;
import ro.uaic.feaa.sdbis.supplychain.facturi.domain.OrdinPlata;
import ro.uaic.feaa.sdbis.supplychain.facturi.domain.OrdinPlataRepo;
import ro.uaic.feaa.sdbis.supplychain.facturi.domain.StareFactura;
import ro.uaic.feaa.sdbis.supplychain.facturi.domain.StareOrdinPlata;
import ro.uaic.feaa.sdbis.supplychain.shared.IdComanda;
import ro.uaic.feaa.sdbis.supplychain.shared.IdFurnizor;
import ro.uaic.feaa.sdbis.supplychain.shared.annotation.QueryService;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
@QueryService
public class FacturaQueryService {

    private final FacturaRepo facturaRepo;
    private final FurnizoriBlocatiRepo furnizoriBlocatiRepo;
    private final NIRRepo nirRepo;
    private final OrdinPlataRepo ordinPlataRepo;

    public FacturaQueryService(FacturaRepo facturaRepo,
                               FurnizoriBlocatiRepo furnizoriBlocatiRepo,
                               NIRRepo nirRepo,
                               OrdinPlataRepo ordinPlataRepo) {
        this.facturaRepo = facturaRepo;
        this.furnizoriBlocatiRepo = furnizoriBlocatiRepo;
        this.nirRepo = nirRepo;
        this.ordinPlataRepo = ordinPlataRepo;
    }

    public List<ScadentarFacturiView> obtineScadentar() {
        List<FacturaFurnizor> facturi = facturaRepo.findFacturiNeachitate(200);
        Set<IdFurnizor> furnizoriBlocati = furnizoriBlocatiRepo.obtineTotiFurnizoriiBlocati();
        List<ScadentarFacturiView> views = new ArrayList<>(facturi.size());
        LocalDate azi = LocalDate.now();

        for (FacturaFurnizor f : facturi) {
            long zilePanaLaScadenta = ChronoUnit.DAYS.between(azi, f.getDataScadenta());
            boolean blocat = furnizoriBlocati.contains(f.getFurnizorId());
            boolean alerta = zilePanaLaScadenta <= 3 && f.calculeazaRestDePlata().getValoare().signum() > 0;

            views.add(new ScadentarFacturiView(
                f.getIdFactura(),
                f.getSerie() + "-" + f.getNumar(),
                f.getFurnizorId().getValue(),
                f.getComandaId().getValue(),
                f.getDataEmitere(),
                f.getDataScadenta(),
                zilePanaLaScadenta,
                f.getValoare().getValoareTotala(),
                f.getSumaPlatita().getValoare(),
                f.calculeazaRestDePlata().getValoare(),
                f.getStareFactura(),
                blocat,
                alerta
            ));
        }
        return views;
    }

    public List<FacturaListItemView> listeazaFacturi(UUID furnizorId, UUID comandaId, String stare, int page, int size) {
        IdFurnizor fId = furnizorId != null ? IdFurnizor.of(furnizorId) : null;
        IdComanda cId = comandaId != null ? IdComanda.of(comandaId) : null;
        StareFactura st = stare != null && !stare.isBlank() ? StareFactura.valueOf(stare.trim()) : null;
        List<FacturaFurnizor> facturi = facturaRepo.findFacturi(fId, cId, st, page, size);
        Set<IdFurnizor> furnizoriBlocati = furnizoriBlocatiRepo.obtineTotiFurnizoriiBlocati();
        return facturi.stream()
            .map(f -> {
                boolean blocat = furnizoriBlocati.contains(f.getFurnizorId());
                return new FacturaListItemView(
                    f.getIdFactura(),
                    f.getSerie() + "-" + f.getNumar(),
                    f.getFurnizorId().getValue(),
                    f.getComandaId().getValue(),
                    f.getDataEmitere(),
                    f.getDataScadenta(),
                    f.getValoare().getValoareTotala(),
                    f.getSumaPlatita().getValoare(),
                    f.calculeazaRestDePlata().getValoare(),
                    f.getSumaPlatita().getMoneda().getCurrencyCode(),
                    f.getStareFactura(),
                    blocat
                );
            })
            .toList();
    }

    public java.util.Optional<FacturaDetaliiView> obtineDetaliiFactura(UUID id) {
        return facturaRepo.findById(id).map(f -> {
            boolean blocat = furnizoriBlocatiRepo.esteBlocat(f.getFurnizorId());
            var linii = f.getLinii().stream()
                .map(l -> new FacturaDetaliiView.LinieFacturaDetaliiItemView(
                    l.getMaterial().getCod(),
                    l.getCantitate(),
                    l.getUnitateMasura(),
                    l.getPretUnitar().getValoare(),
                    l.getPretUnitar().getMoneda().getCurrencyCode(),
                    l.getCotaTVA()
                ))
                .toList();

            return new FacturaDetaliiView(
                f.getIdFactura(),
                f.getSerie(),
                f.getNumar(),
                f.getFurnizorId().getValue(),
                f.getComandaId().getValue(),
                f.getDataEmitere(),
                f.getDataScadenta(),
                f.getValoare().getValoareFaraTVA(),
                f.getValoare().getValoareTVA(),
                f.getValoare().getValoareTotala(),
                f.getSumaPlatita().getValoare(),
                f.calculeazaRestDePlata().getValoare(),
                f.getSumaPlatita().getMoneda().getCurrencyCode(),
                f.getStareFactura(),
                blocat,
                linii
            );
        });
    }

    public List<NIRListItemView> listeazaNIR(UUID comandaId, int page, int size) {
        IdComanda cId = comandaId != null ? IdComanda.of(comandaId) : null;
        List<NIR> nirs = nirRepo.findNIR(cId, page, size);
        return nirs.stream()
            .map(n -> {
                var linii = n.getLinii().stream()
                    .map(l -> new NIRListItemView.LinieNIRView(
                        l.getIdLot().getValue(),
                        l.getMaterial().getCod(),
                        l.getCantitateReceptionata(),
                        l.getUnitateMasura()
                    ))
                    .toList();
                return new NIRListItemView(
                    n.getIdNIR(),
                    n.getNumarNIR(),
                    n.getComandaId().getValue(),
                    n.getEventSursaId(),
                    n.getDataReceptie(),
                    n.getStareNIR(),
                    linii
                );
            })
            .toList();
    }

    public java.util.Optional<NIRListItemView> obtineDetaliiNIR(UUID id) {
        return nirRepo.findById(id).map(n -> {
            var linii = n.getLinii().stream()
                .map(l -> new NIRListItemView.LinieNIRView(
                    l.getIdLot().getValue(),
                    l.getMaterial().getCod(),
                    l.getCantitateReceptionata(),
                    l.getUnitateMasura()
                ))
                .toList();
            return new NIRListItemView(
                n.getIdNIR(),
                n.getNumarNIR(),
                n.getComandaId().getValue(),
                n.getEventSursaId(),
                n.getDataReceptie(),
                n.getStareNIR(),
                linii
            );
        });
    }

    public List<OrdinPlataListItemView> listeazaOrdinePlata(UUID facturaId, String stare, int page, int size) {
        StareOrdinPlata st = stare != null && !stare.isBlank() ? StareOrdinPlata.valueOf(stare.trim()) : null;
        List<OrdinPlata> ordine = ordinPlataRepo.findOrdinePlata(facturaId, st, page, size);
        return ordine.stream()
            .map(o -> new OrdinPlataListItemView(
                o.getIdOrdin(),
                o.getFacturaId(),
                o.getSuma().getValoare(),
                o.getSuma().getMoneda().getCurrencyCode(),
                o.getCreatDe(),
                o.getAprobatDe(),
                o.getStareOrdin(),
                o.getReferintaBancara(),
                o.getMotivRespingere(),
                o.getDataAprobare(),
                o.getDataExecutie()
            ))
            .toList();
    }

    public java.util.Optional<OrdinPlataListItemView> obtineDetaliiOrdinPlata(UUID id) {
        return ordinPlataRepo.findById(id).map(o -> new OrdinPlataListItemView(
            o.getIdOrdin(),
            o.getFacturaId(),
            o.getSuma().getValoare(),
            o.getSuma().getMoneda().getCurrencyCode(),
            o.getCreatDe(),
            o.getAprobatDe(),
            o.getStareOrdin(),
            o.getReferintaBancara(),
            o.getMotivRespingere(),
            o.getDataAprobare(),
            o.getDataExecutie()
        ));
    }
}
