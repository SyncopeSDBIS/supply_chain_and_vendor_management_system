package ro.uaic.feaa.sdbis.supplychain.comenzi.presentation;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ro.uaic.feaa.sdbis.supplychain.comenzi.application.ComandaApplicationService;
import ro.uaic.feaa.sdbis.supplychain.comenzi.application.command.*;
import ro.uaic.feaa.sdbis.supplychain.comenzi.application.dto.CreeazaComandaRequest;
import ro.uaic.feaa.sdbis.supplychain.comenzi.application.query.ComandaQueryService;
import ro.uaic.feaa.sdbis.supplychain.comenzi.domain.Cantitate;
import ro.uaic.feaa.sdbis.supplychain.comenzi.domain.LinieComanda;
import ro.uaic.feaa.sdbis.supplychain.comenzi.domain.PretUnitar;
import ro.uaic.feaa.sdbis.supplychain.comenzi.domain.RezultatCalitateLot;
import ro.uaic.feaa.sdbis.supplychain.shared.CodMaterial;
import ro.uaic.feaa.sdbis.supplychain.shared.IdComanda;
import ro.uaic.feaa.sdbis.supplychain.shared.IdFurnizor;
import ro.uaic.feaa.sdbis.supplychain.shared.IdLot;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/comenzi")
public class ComandaCtrl {

    private final ComandaApplicationService appService;
    private final ComandaQueryService queryService;

    public ComandaCtrl(ComandaApplicationService appService, ComandaQueryService queryService) {
        this.appService = appService;
        this.queryService = queryService;
    }

    @PostMapping(consumes = {"application/json", "application/xml"}, produces = {"application/json", "application/xml"})
    public ResponseEntity<?> creeazaComanda(@RequestBody CreeazaComandaRequest req) {
        try {
            List<LinieComanda> linii = req.getLinii().stream()
                .map(l -> new LinieComanda(
                    CodMaterial.of(l.getMaterialCod()),
                    Cantitate.of(l.getCantitate(), l.getUnitateMasura()),
                    PretUnitar.of(l.getPretUnitar(), l.getMoneda())
                ))
                .collect(Collectors.toList());

            CreeazaComandaCommand cmd = new CreeazaComandaCommand(
                req.getNumarComanda(),
                IdFurnizor.of(req.getFurnizorId()),
                req.getCumparatorId(),
                req.getDataComanda() != null ? req.getDataComanda() : LocalDate.now(),
                req.getDataLivrareCeruta(),
                req.getDepozit(),
                linii
            );

            IdComanda id = appService.creeazaComanda(cmd);
            Map<String, Object> resp = new HashMap<>();
            resp.put("comandaId", id.getValue());
            resp.put("numarComanda", req.getNumarComanda());
            resp.put("status", "Solicitat");
            return new ResponseEntity<>(resp, HttpStatus.CREATED);
        } catch (IllegalStateException e) {
            Map<String, String> err = new HashMap<>();
            err.put("error", e.getMessage());
            return new ResponseEntity<>(err, HttpStatus.UNPROCESSABLE_ENTITY);
        } catch (Exception e) {
            Map<String, String> err = new HashMap<>();
            err.put("error", e.getMessage());
            return new ResponseEntity<>(err, HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> editeazaComanda(@PathVariable UUID id,
                                             @RequestParam(required = false) String dataLivrareCeruta,
                                             @RequestParam(required = false) String depozit) {
        try {
            LocalDate dt = dataLivrareCeruta != null ? LocalDate.parse(dataLivrareCeruta) : null;
            appService.editeazaComanda(IdComanda.of(id), dt, depozit);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> stergeComanda(@PathVariable UUID id) {
        try {
            appService.stergeComanda(IdComanda.of(id));
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/{id}/confirmare")
    public ResponseEntity<?> confirmaComanda(@PathVariable UUID id, @RequestParam String dataPromisa) {
        try {
            appService.confirmaComanda(new ConfirmaComandaCommand(IdComanda.of(id), LocalDate.parse(dataPromisa)));
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/{id}/refuz")
    public ResponseEntity<?> refuzaComanda(@PathVariable UUID id) {
        try {
            appService.refuzaComanda(IdComanda.of(id));
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/{id}/expedieri")
    public ResponseEntity<?> inregistreazaExpediere(@PathVariable UUID id,
                                                    @RequestParam String transportator,
                                                    @RequestParam String awb,
                                                    @RequestBody(required = false) Set<UUID> loturiIds) {
        try {
            Set<IdLot> loturi = new HashSet<>();
            if (loturiIds != null) {
                for (UUID lid : loturiIds) loturi.add(IdLot.of(lid));
            }
            appService.inregistreazaExpediere(new InregistreazaExpediereCommand(IdComanda.of(id), transportator, awb, loturi));
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/{id}/receptii")
    public ResponseEntity<?> inregistreazaReceptie(@PathVariable UUID id,
                                                   @RequestParam UUID lotId,
                                                   @RequestParam double cantitate,
                                                   @RequestParam String unitate) {
        try {
            appService.inregistreazaReceptie(new InregistreazaReceptieCommand(
                IdComanda.of(id), IdLot.of(lotId), Cantitate.of(cantitate, unitate)
            ));
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/{id}/anulare")
    public ResponseEntity<?> anuleazaComanda(@PathVariable UUID id) {
        try {
            appService.anuleazaComanda(new AnuleazaComandaCommand(IdComanda.of(id)));
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/{id}/estimare-livrare")
    public ResponseEntity<?> estimeazaDataLivrare(@PathVariable UUID id) {
        try {
            appService.estimeazaDataLivrare(IdComanda.of(id));
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/{id}/loturi")
    public ResponseEntity<?> planificaLot(@PathVariable UUID id,
                                          @RequestParam String codLot,
                                          @RequestParam String materialCod,
                                          @RequestParam double cantitate,
                                          @RequestParam String unitate,
                                          @RequestParam String dataProductie,
                                          @RequestParam String dataExpirare) {
        try {
            IdLot lotId = appService.planificaLot(
                IdComanda.of(id),
                codLot,
                CodMaterial.of(materialCod),
                Cantitate.of(cantitate, unitate),
                LocalDate.parse(dataProductie),
                LocalDate.parse(dataExpirare)
            );
            Map<String, Object> resp = new HashMap<>();
            resp.put("lotId", lotId.getValue());
            resp.put("codLot", codLot);
            return new ResponseEntity<>(resp, HttpStatus.CREATED);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/{id}/loturi/{lotId}/carantina")
    public ResponseEntity<?> carantinaLot(@PathVariable UUID id, @PathVariable UUID lotId) {
        try {
            appService.plaseazaLotInCarantina(IdComanda.of(id), IdLot.of(lotId));
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/{id}/loturi/{lotId}/verdict")
    public ResponseEntity<?> aplicaVerdictInspectie(@PathVariable UUID id,
                                                    @PathVariable UUID lotId,
                                                    @RequestParam String rezultat) {
        try {
            RezultatCalitateLot res = RezultatCalitateLot.valueOf(rezultat);
            appService.aplicaVerdictInspectie(IdComanda.of(id), IdLot.of(lotId), res);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/{id}/loturi/{lotId}/retur")
    public ResponseEntity<?> returneazaLot(@PathVariable UUID id, @PathVariable UUID lotId) {
        try {
            appService.returneazaLotFurnizorului(IdComanda.of(id), IdLot.of(lotId));
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/loturi/{codLot}/trasabilitate")
    public ResponseEntity<?> obtineTrasabilitateLot(@PathVariable String codLot) {
        return queryService.obtineTrasabilitateLot(codLot)
            .map(ResponseEntity::ok)
            .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/rapoarte/otif")
    public ResponseEntity<?> obtineDashboardLivrari(@RequestParam(required = false) UUID furnizorId) {
        return ResponseEntity.ok(queryService.obtineDashboardLivrari(furnizorId));
    }
}
