package ro.uaic.feaa.sdbis.supplychain.facturi.presentation;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ro.uaic.feaa.sdbis.supplychain.facturi.application.FacturaApplicationService;
import ro.uaic.feaa.sdbis.supplychain.facturi.application.command.CreeazaOrdinPlataCommand;
import ro.uaic.feaa.sdbis.supplychain.facturi.application.command.InregistreazaFacturaCommand;
import ro.uaic.feaa.sdbis.supplychain.facturi.application.command.ReconciliereCommand;
import ro.uaic.feaa.sdbis.supplychain.facturi.application.command.TransmiteOrdinCommand;
import ro.uaic.feaa.sdbis.supplychain.facturi.application.query.FacturaQueryService;
import ro.uaic.feaa.sdbis.supplychain.facturi.domain.LinieFactura;
import ro.uaic.feaa.sdbis.supplychain.facturi.domain.Suma;
import ro.uaic.feaa.sdbis.supplychain.shared.CodMaterial;
import ro.uaic.feaa.sdbis.supplychain.shared.IdComanda;
import ro.uaic.feaa.sdbis.supplychain.shared.IdFurnizor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

@RestController
@RequestMapping("/api")
public class FacturaCtrl {

    private final FacturaApplicationService appService;
    private final FacturaQueryService queryService;

    public FacturaCtrl(FacturaApplicationService appService, FacturaQueryService queryService) {
        this.appService = appService;
        this.queryService = queryService;
    }

    @PostMapping("/facturi")
    public ResponseEntity<?> inregistreazaFactura(@RequestParam String serie,
                                                  @RequestParam String numar,
                                                  @RequestParam String dataEmitere,
                                                  @RequestParam String dataScadenta,
                                                  @RequestParam UUID comandaId,
                                                  @RequestParam UUID furnizorId,
                                                  @RequestParam(defaultValue = "EUR") String moneda,
                                                  @RequestParam String materialCod,
                                                  @RequestParam double cantitate,
                                                  @RequestParam String unitate,
                                                  @RequestParam double pretUnitar) {
        try {
            LinieFactura linie = new LinieFactura(
                CodMaterial.of(materialCod),
                BigDecimal.valueOf(cantitate),
                unitate,
                Suma.of(pretUnitar, moneda),
                BigDecimal.valueOf(0.19)
            );

            UUID id = appService.inregistreazaFactura(new InregistreazaFacturaCommand(
                serie, numar, LocalDate.parse(dataEmitere), LocalDate.parse(dataScadenta),
                IdComanda.of(comandaId), IdFurnizor.of(furnizorId),
                Collections.singletonList(linie), Currency.getInstance(moneda)
            ));

            Map<String, Object> resp = new HashMap<>();
            resp.put("facturaId", id);
            resp.put("status", "Inregistrata");
            return new ResponseEntity<>(resp, HttpStatus.CREATED);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/facturi/{id}/contestare")
    public ResponseEntity<?> contestaFactura(@PathVariable UUID id, @RequestParam String motiv) {
        try {
            appService.contestaFactura(id, motiv);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/facturi/{id}/reconciliere")
    public ResponseEntity<?> reconcilieazaFactura(@PathVariable UUID id) {
        try {
            appService.reconcilieazaFactura(new ReconciliereCommand(id));
            Map<String, String> resp = new HashMap<>();
            resp.put("status", "SUCCESS");
            resp.put("message", "Reconcilierea a fost executata");
            return ResponseEntity.ok(resp);
        } catch (IllegalStateException e) {
            Map<String, String> err = new HashMap<>();
            err.put("error", e.getMessage());
            return new ResponseEntity<>(err, HttpStatus.CONFLICT);
        } catch (Exception e) {
            Map<String, String> err = new HashMap<>();
            err.put("error", e.getMessage());
            return new ResponseEntity<>(err, HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/nir/{id}/contabilizare")
    public ResponseEntity<?> contabilizeazaNIR(@PathVariable UUID id) {
        try {
            appService.contabilizeazaNIR(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/nir/{id}/stornare")
    public ResponseEntity<?> storneazaNIR(@PathVariable UUID id, @RequestParam String motiv) {
        try {
            appService.storneazaNIR(id, motiv);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/facturi/{id}/ordine-plata")
    public ResponseEntity<?> creeazaOrdinPlata(@PathVariable UUID id,
                                                @RequestParam double suma,
                                                @RequestParam String moneda,
                                                @RequestParam UUID creatDe) {
        try {
            UUID ordinId = appService.creeazaOrdinPlata(new CreeazaOrdinPlataCommand(id, Suma.of(suma, moneda), creatDe));
            Map<String, Object> resp = new HashMap<>();
            resp.put("ordinId", ordinId);
            resp.put("facturaId", id);
            return new ResponseEntity<>(resp, HttpStatus.CREATED);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/ordine-plata/{id}/aprobare")
    public ResponseEntity<?> aprobaOrdin(@PathVariable UUID id, @RequestParam UUID aprobatorId) {
        try {
            appService.aprobaOrdin(id, aprobatorId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/ordine-plata/{id}/transmitere")
    public ResponseEntity<?> transmiteOrdin(@PathVariable UUID id, @RequestParam String iban) {
        try {
            appService.transmiteOrdin(new TransmiteOrdinCommand(id, iban));
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/ordine-plata/webhook-banca")
    public ResponseEntity<?> webhookBanca(@RequestParam UUID ordinId) {
        try {
            appService.inregistreazaExecutiePlata(ordinId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/facturi/scadente")
    public ResponseEntity<?> obtineScadentar() {
        return ResponseEntity.ok(queryService.obtineScadentar());
    }
}
