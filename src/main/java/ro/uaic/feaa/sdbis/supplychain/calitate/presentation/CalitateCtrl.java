package ro.uaic.feaa.sdbis.supplychain.calitate.presentation;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ro.uaic.feaa.sdbis.supplychain.calitate.application.CalitateApplicationService;
import ro.uaic.feaa.sdbis.supplychain.calitate.application.command.AlocaInspectorCommand;
import ro.uaic.feaa.sdbis.supplychain.calitate.application.command.CalculScorecardCommand;
import ro.uaic.feaa.sdbis.supplychain.calitate.application.command.IncepeInspectieCommand;
import ro.uaic.feaa.sdbis.supplychain.calitate.application.command.InregistreazaVerdictCommand;
import ro.uaic.feaa.sdbis.supplychain.calitate.application.query.CalitateQueryService;
import ro.uaic.feaa.sdbis.supplychain.calitate.domain.*;
import ro.uaic.feaa.sdbis.supplychain.shared.IdFurnizor;
import ro.uaic.feaa.sdbis.supplychain.shared.Perioada;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

@RestController
@RequestMapping("/api")
public class CalitateCtrl {

    private final CalitateApplicationService appService;
    private final CalitateQueryService queryService;

    public CalitateCtrl(CalitateApplicationService appService, CalitateQueryService queryService) {
        this.appService = appService;
        this.queryService = queryService;
    }

    @PostMapping("/furnizori")
    public ResponseEntity<?> inregistreazaFurnizor(@RequestParam String codFiscal,
                                                   @RequestParam String denumire,
                                                   @RequestParam(required = false) String iban,
                                                   @RequestParam(required = false) String email) {
        try {
            Iban ibanVo = iban != null ? new Iban(iban) : null;
            Set<Email> emails = email != null ? Collections.singleton(new Email(email)) : Collections.emptySet();
            IdFurnizor id = appService.inregistreazaFurnizor(codFiscal, denumire, null, emails, ibanVo, Collections.emptySet(), null);
            Map<String, Object> resp = new HashMap<>();
            resp.put("furnizorId", id.getValue());
            resp.put("codFiscal", codFiscal);
            resp.put("status", "Candidat");
            return new ResponseEntity<>(resp, HttpStatus.CREATED);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/furnizori/{id}")
    public ResponseEntity<?> actualizeazaProfilFurnizor(@PathVariable UUID id,
                                                        @RequestParam(required = false) String denumire,
                                                        @RequestParam(required = false) String iban) {
        try {
            Iban ibanVo = iban != null ? new Iban(iban) : null;
            appService.actualizeazaProfilFurnizor(IdFurnizor.of(id), denumire, null, null, ibanVo, null, null);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/furnizori/{id}/calificare")
    public ResponseEntity<?> calificaFurnizor(@PathVariable UUID id,
                                              @RequestParam String decizie,
                                              @RequestParam(required = false, defaultValue = "") String motiv) {
        try {
            appService.calificaFurnizor(IdFurnizor.of(id), decizie, motiv);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/inspectori")
    public ResponseEntity<?> creeazaInspector(@RequestParam String firstName, @RequestParam String lastName) {
        try {
            UUID id = appService.creeazaInspector(firstName, lastName);
            Map<String, Object> resp = new HashMap<>();
            resp.put("inspectorId", id);
            return new ResponseEntity<>(resp, HttpStatus.CREATED);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/inspectori/{id}/certificari")
    public ResponseEntity<?> adaugaCertificare(@PathVariable UUID id,
                                               @RequestParam String denumire,
                                               @RequestParam String dataExpirare) {
        try {
            appService.adaugaCertificareInspector(id, denumire, LocalDate.parse(dataExpirare));
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/inspectori/{id}/disponibilitate")
    public ResponseEntity<?> declaraDisponibilitate(@PathVariable UUID id,
                                                    @RequestParam String deLa,
                                                    @RequestParam String panaLa) {
        try {
            appService.declaraDisponibilitateInspector(id, LocalDate.parse(deLa), LocalDate.parse(panaLa));
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/inspectii/{id}/alocare")
    public ResponseEntity<?> alocaInspector(@PathVariable UUID id, @RequestParam UUID inspectorId) {
        try {
            appService.alocaInspector(new AlocaInspectorCommand(id, inspectorId));
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/inspectii/{id}/start")
    public ResponseEntity<?> incepeInspectie(@PathVariable UUID id) {
        try {
            appService.incepeInspectie(new IncepeInspectieCommand(id));
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/inspectii/{id}/verdict")
    public ResponseEntity<?> inregistreazaVerdict(@PathVariable UUID id, @RequestParam int defecteGasite) {
        try {
            appService.inregistreazaVerdict(new InregistreazaVerdictCommand(id, defecteGasite));
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/inspectii/{id}/reinspectie")
    public ResponseEntity<?> solicitaReinspectie(@PathVariable UUID id) {
        try {
            appService.solicitaReinspectie(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/ncr/{id}/cauza")
    public ResponseEntity<?> analizeazaCauzaNCR(@PathVariable UUID id, @RequestParam String cauza) {
        try {
            appService.analizeazaCauzaNCR(id, cauza);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/ncr/{id}/actiune")
    public ResponseEntity<?> initiazaActiuneNCR(@PathVariable UUID id,
                                                @RequestParam String descriere,
                                                @RequestParam String responsabil,
                                                @RequestParam String termen) {
        try {
            appService.initiazaActiuneNCR(id, descriere, responsabil, LocalDate.parse(termen));
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/ncr/{id}/verificare")
    public ResponseEntity<?> verificaNCR(@PathVariable UUID id) {
        try {
            appService.verificaNCR(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/ncr/{id}/inchidere")
    public ResponseEntity<?> inchideNCR(@PathVariable UUID id) {
        try {
            appService.inchideNCR(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/ncr/{id}/escaladare")
    public ResponseEntity<?> escaladeazaNCR(@PathVariable UUID id) {
        try {
            appService.escaladeazaNCR(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/furnizori/{id}/scorecard-lunar")
    public ResponseEntity<?> calculeazaScorecard(@PathVariable UUID id,
                                                @RequestParam int an,
                                                @RequestParam int luna,
                                                @RequestParam(defaultValue = "2.0") double rataRespingere) {
        try {
            LocalDate start = LocalDate.of(an, luna, 1);
            LocalDate end = start.plusMonths(1).minusDays(1);
            appService.calculeazaScorecardLunar(new CalculScorecardCommand(
                IdFurnizor.of(id), new Perioada(start, end), BigDecimal.valueOf(rataRespingere)
            ));
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/furnizori/rapoarte/monitorizare")
    public ResponseEntity<?> obtineMonitorizareFurnizori() {
        return ResponseEntity.ok(queryService.obtineMonitorizareFurnizori());
    }
}
