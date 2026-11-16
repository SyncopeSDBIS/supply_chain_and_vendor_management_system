package ro.uaic.feaa.sdbis.supplychain.comenzi.application;

import ro.uaic.feaa.sdbis.supplychain.comenzi.application.command.CreeazaComandaCommand;
import ro.uaic.feaa.sdbis.supplychain.comenzi.application.dto.CreeazaComandaRequest;
import ro.uaic.feaa.sdbis.supplychain.comenzi.domain.Cantitate;
import ro.uaic.feaa.sdbis.supplychain.comenzi.domain.LinieComanda;
import ro.uaic.feaa.sdbis.supplychain.comenzi.domain.PretUnitar;
import ro.uaic.feaa.sdbis.supplychain.shared.CodMaterial;
import ro.uaic.feaa.sdbis.supplychain.shared.IdFurnizor;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Factory in Application Layer responsabila de asamblarea comenzilor de domeniu din DTO-urile de prezentare/REST.
 * Previne scurgerea asamblarii obiectelor de domeniu (LinieComanda, Cantitate, PretUnitar) in controlerele REST.
 */
public final class ComandaCommandFactory {

    private ComandaCommandFactory() {}

    public static CreeazaComandaCommand fromRequest(CreeazaComandaRequest req) {
        return fromRequest(req, req.getCumparatorId());
    }

    public static CreeazaComandaCommand fromRequest(CreeazaComandaRequest req, java.util.UUID cumparatorId) {
        List<LinieComanda> linii = req.getLinii() != null
                ? req.getLinii().stream()
                .map(l -> new LinieComanda(
                        CodMaterial.of(l.getMaterialCod()),
                        Cantitate.of(l.getCantitate(), l.getUnitateMasura()),
                        PretUnitar.of(l.getPretUnitar(), l.getMoneda())
                ))
                .collect(Collectors.toList())
                : Collections.emptyList();

        return new CreeazaComandaCommand(
                req.getNumarComanda(),
                IdFurnizor.of(req.getFurnizorId()),
                cumparatorId != null ? cumparatorId : req.getCumparatorId(),
                req.getDataComanda() != null ? req.getDataComanda() : LocalDate.now(),
                req.getDataLivrareCeruta(),
                req.getDepozit(),
                linii
        );
    }
}

