package ro.uaic.feaa.sdbis.supplychain.calitate.presentation;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
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
@Validated
@RequestMapping("/api")
public class CalitateCtrl {

    private final CalitateApplicationService appService;
    private final CalitateQueryService queryService;

    public CalitateCtrl(CalitateApplicationService appService, CalitateQueryService queryService) {
        this.appService = appService;
        this.queryService = queryService;
    }

    @PostMapping(value = "/furnizori", consumes = org.springframework.http.MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> inregistreazaFurnizorJson(@Valid @RequestBody InregistreazaFurnizorRequest req) {
        return processInregistreazaFurnizor(req);
    }

    @PostMapping(value = "/furnizori")
    public ResponseEntity<?> inregistreazaFurnizorForm(@Valid @ModelAttribute InregistreazaFurnizorRequest req) {
        return processInregistreazaFurnizor(req);
    }

    private ResponseEntity<?> processInregistreazaFurnizor(InregistreazaFurnizorRequest req) {
        if (req.getCodFiscal() == null || req.getDenumire() == null) {
            return ResponseEntity.badRequest().body("Codul fiscal si denumirea sunt obligatorii");
        }

        try {
            Iban ibanVo = req.getIban() != null && !req.getIban().isBlank() ? new Iban(req.getIban()) : null;
            Set<Email> emails = req.getEmail() != null && !req.getEmail().isBlank()
                    ? Collections.singleton(new Email(req.getEmail()))
                    : Collections.emptySet();
            IdFurnizor id = appService.inregistreazaFurnizor(req.getCodFiscal(), req.getDenumire(), null, emails, ibanVo, Collections.emptySet(), null);
            Map<String, Object> resp = new HashMap<>();
            resp.put("furnizorId", id.getValue());
            resp.put("codFiscal", req.getCodFiscal());
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
            Iban ibanVo = iban != null && !iban.isBlank() ? new Iban(iban) : null;
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

    @PostMapping(value = "/inspectori", consumes = org.springframework.http.MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> creeazaInspectorJson(@Valid @RequestBody CreeazaInspectorRequest req) {
        return processCreeazaInspector(req);
    }

    @PostMapping(value = "/inspectori")
    public ResponseEntity<?> creeazaInspectorForm(@Valid @ModelAttribute CreeazaInspectorRequest req) {
        return processCreeazaInspector(req);
    }

    private ResponseEntity<?> processCreeazaInspector(CreeazaInspectorRequest req) {
        if (req.getFirstName() == null || req.getLastName() == null) {
            return ResponseEntity.badRequest().body("Prenumele si numele inspectorului sunt obligatorii");
        }

        try {
            UUID id = appService.creeazaInspector(req.getFirstName(), req.getLastName());
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
    public ResponseEntity<?> inregistreazaVerdict(
            @PathVariable UUID id,
            @Valid @RequestBody(required = false) InregistreazaVerdictRequest bodyReq,
            @Valid @ModelAttribute InregistreazaVerdictRequest formReq) {
        InregistreazaVerdictRequest req = (bodyReq != null) ? bodyReq : formReq;
        try {
            appService.inregistreazaVerdict(new InregistreazaVerdictCommand(id, req.getDefecteGasite()));
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
    public ResponseEntity<?> initiazaActiuneNCR(
            @PathVariable UUID id,
            @Valid @RequestBody(required = false) InitiazaActiuneNCRRequest bodyReq,
            @Valid @ModelAttribute InitiazaActiuneNCRRequest formReq) {
        InitiazaActiuneNCRRequest req = (bodyReq != null && bodyReq.getDescriere() != null) ? bodyReq : formReq;
        if (req.getDescriere() == null || req.getResponsabil() == null || req.getTermen() == null) {
            return ResponseEntity.badRequest().body("Descrierea, responsabilul si termenul sunt obligatorii");
        }

        try {
            appService.initiazaActiuneNCR(id, req.getDescriere(), req.getResponsabil(), LocalDate.parse(req.getTermen()));
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
    public ResponseEntity<?> calculeazaScorecard(
            @PathVariable UUID id,
            @Valid @RequestBody(required = false) CalculScorecardRequest bodyReq,
            @Valid @ModelAttribute CalculScorecardRequest formReq) {
        CalculScorecardRequest req = (bodyReq != null && bodyReq.getAn() > 0) ? bodyReq : formReq;
        if (req.getAn() <= 0 || req.getLuna() <= 0) {
            return ResponseEntity.badRequest().body("Anul si luna sunt obligatorii");
        }

        try {
            LocalDate start = LocalDate.of(req.getAn(), req.getLuna(), 1);
            LocalDate end = start.plusMonths(1).minusDays(1);
            appService.calculeazaScorecardLunar(new CalculScorecardCommand(
                IdFurnizor.of(id), new Perioada(start, end), BigDecimal.valueOf(req.getRataRespingere())
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
