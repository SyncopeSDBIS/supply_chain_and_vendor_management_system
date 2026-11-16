package ro.uaic.feaa.sdbis.supplychain.calitate.presentation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ro.uaic.feaa.sdbis.supplychain.calitate.application.CalitateApplicationService;
import ro.uaic.feaa.sdbis.supplychain.calitate.application.command.AlocaInspectorCommand;
import ro.uaic.feaa.sdbis.supplychain.calitate.application.command.CalculScorecardCommand;
import ro.uaic.feaa.sdbis.supplychain.calitate.application.command.IncepeInspectieCommand;
import ro.uaic.feaa.sdbis.supplychain.calitate.application.command.InregistreazaVerdictCommand;
import ro.uaic.feaa.sdbis.supplychain.calitate.application.query.CalitateQueryService;
import ro.uaic.feaa.sdbis.supplychain.calitate.application.query.FurnizorDetaliiView;
import ro.uaic.feaa.sdbis.supplychain.calitate.application.query.FurnizorListItemView;
import ro.uaic.feaa.sdbis.supplychain.calitate.application.query.InspectieListItemView;
import ro.uaic.feaa.sdbis.supplychain.calitate.application.query.InspectorListItemView;
import ro.uaic.feaa.sdbis.supplychain.calitate.application.query.MonitorizareFurnizoriView;
import ro.uaic.feaa.sdbis.supplychain.calitate.application.query.NCRListItemView;
import ro.uaic.feaa.sdbis.supplychain.calitate.domain.Adresa;
import ro.uaic.feaa.sdbis.supplychain.calitate.domain.ConditiiContractuale;
import ro.uaic.feaa.sdbis.supplychain.calitate.domain.Email;
import ro.uaic.feaa.sdbis.supplychain.calitate.domain.Iban;
import ro.uaic.feaa.sdbis.supplychain.calitate.domain.computation.CalitateDashboardView;
import ro.uaic.feaa.sdbis.supplychain.shared.IdFurnizor;
import ro.uaic.feaa.sdbis.supplychain.shared.Perioada;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Currency;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@RestController
@Validated
@RequestMapping("/api")
@Tag(name = "Calitate", description = "Management calitate, eligibilitate furnizori, inspectii AQL si rapoarte NCR")
public class CalitateCtrl {

    private final CalitateApplicationService appService;
    private final CalitateQueryService queryService;

    public CalitateCtrl(CalitateApplicationService appService, CalitateQueryService queryService) {
        this.appService = appService;
        this.queryService = queryService;
    }

    @PostMapping(value = "/furnizori", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Inregistreaza un furnizor nou (JSON payload)")
    public ResponseEntity<FurnizorInregistratResponse> inregistreazaFurnizorJson(@Valid @RequestBody InregistreazaFurnizorRequest req) {
        return processInregistreazaFurnizor(req);
    }

    @PostMapping(value = "/furnizori")
    @Operation(summary = "Inregistreaza un furnizor nou (Form / URL-encoded payload)")
    public ResponseEntity<FurnizorInregistratResponse> inregistreazaFurnizorForm(@Valid @ModelAttribute InregistreazaFurnizorRequest req) {
        return processInregistreazaFurnizor(req);
    }

    private ResponseEntity<FurnizorInregistratResponse> processInregistreazaFurnizor(InregistreazaFurnizorRequest req) {
        if (req.getCodFiscal() == null || req.getCodFiscal().isBlank() || req.getDenumire() == null || req.getDenumire().isBlank()) {
            throw new IllegalArgumentException("Codul fiscal si denumirea sunt obligatorii");
        }

        Iban ibanVo = req.getIban() != null && !req.getIban().isBlank() ? new Iban(req.getIban()) : null;

        Set<Email> emails = new HashSet<>();
        if (req.getEmail() != null && !req.getEmail().isBlank()) {
            emails.add(new Email(req.getEmail()));
        }
        if (req.getEmailuri() != null) {
            for (String em : req.getEmailuri()) {
                if (em != null && !em.isBlank()) {
                    emails.add(new Email(em));
                }
            }
        }

        Set<String> categorii = req.getCategorii();
        if (categorii == null) {
            categorii = Collections.emptySet();
        }

        Adresa adresaVo = null;
        if (req.getAdresa() != null) {
            var a = req.getAdresa();
            boolean areDate = (a.getStrada() != null && !a.getStrada().isBlank())
                    || (a.getLocalitate() != null && !a.getLocalitate().isBlank())
                    || (a.getCodPostal() != null && !a.getCodPostal().isBlank());
            if (areDate) {
                String strada = a.getStrada() != null && !a.getStrada().isBlank() ? a.getStrada() : "";
                String numar = a.getNumar() != null && !a.getNumar().isBlank() ? a.getNumar() : "";
                String localitate = a.getLocalitate() != null && !a.getLocalitate().isBlank() ? a.getLocalitate() : "";
                String codPostal = a.getCodPostal() != null && !a.getCodPostal().isBlank() ? a.getCodPostal() : "";
                if (!strada.isBlank() && !numar.isBlank() && !localitate.isBlank() && !codPostal.isBlank()) {
                    adresaVo = new Adresa(strada, numar, localitate, codPostal);
                }
            }
        }

        ConditiiContractuale conditiiVo = null;
        if (req.getTermenPlataZile() != null || (req.getMoneda() != null && !req.getMoneda().isBlank())) {
            int termen = req.getTermenPlataZile() != null ? req.getTermenPlataZile() : 30;
            Currency mon;
            try {
                String rawMoneda = req.getMoneda() != null && !req.getMoneda().isBlank()
                        ? req.getMoneda().trim().toUpperCase() : "EUR";
                mon = Currency.getInstance(rawMoneda);
            } catch (Exception ex) {
                mon = Currency.getInstance("EUR");
            }
            conditiiVo = new ConditiiContractuale(termen, mon, Collections.emptyMap());
        }

        IdFurnizor id = appService.inregistreazaFurnizor(req.getCodFiscal(), req.getDenumire(), adresaVo, emails, ibanVo, categorii, conditiiVo);
        return new ResponseEntity<>(new FurnizorInregistratResponse(id.getValue(), req.getCodFiscal(), "Candidat"), HttpStatus.CREATED);
    }

    @GetMapping(value = "/furnizori/{id}/eligibil", produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
    @Operation(summary = "Interogare eligibilitate furnizor pentru comenzi")
    public ResponseEntity<Boolean> esteFurnizorEligibil(@PathVariable UUID id) {
        boolean eligibil = appService.esteFurnizorEligibil(IdFurnizor.of(id));
        return ResponseEntity.ok(eligibil);
    }

    @PutMapping("/furnizori/{id}")
    @Operation(summary = "Actualizeaza profilul furnizorului")
    public ResponseEntity<Void> actualizeazaProfilFurnizor(@PathVariable UUID id,
                                                          @RequestParam(required = false) String denumire,
                                                          @RequestParam(required = false) String iban) {
        Iban ibanVo = iban != null && !iban.isBlank() ? new Iban(iban) : null;
        appService.actualizeazaProfilFurnizor(IdFurnizor.of(id), denumire, null, null, ibanVo, null, null);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/furnizori/{id}/aprobare")
    @Operation(summary = "Aproba furnizorul ca partener calificat")
    public ResponseEntity<Void> aprobaFurnizor(@PathVariable UUID id) {
        appService.calificaFurnizor(IdFurnizor.of(id), "APROBA", "Aprobat de Director Calitate");
        return ResponseEntity.ok().build();
    }

    @PostMapping("/furnizori/{id}/calificare")
    @Operation(summary = "Califica sau respinge furnizorul")
    public ResponseEntity<Void> calificaFurnizor(@PathVariable UUID id,
                                                @RequestParam String decizie,
                                                @RequestParam(required = false, defaultValue = "") String motiv) {
        appService.calificaFurnizor(IdFurnizor.of(id), decizie, motiv);
        return ResponseEntity.ok().build();
    }

    @PostMapping(value = "/inspectori", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Inregistreaza un inspector de calitate (JSON payload)")
    public ResponseEntity<InspectorCreatResponse> creeazaInspectorJson(@Valid @RequestBody CreeazaInspectorRequest req) {
        return processCreeazaInspector(req);
    }

    @PostMapping(value = "/inspectori")
    @Operation(summary = "Inregistreaza un inspector de calitate (Form payload)")
    public ResponseEntity<InspectorCreatResponse> creeazaInspectorForm(@Valid @ModelAttribute CreeazaInspectorRequest req) {
        return processCreeazaInspector(req);
    }

    private ResponseEntity<InspectorCreatResponse> processCreeazaInspector(CreeazaInspectorRequest req) {
        if (req.getFirstName() == null || req.getFirstName().isBlank() || req.getLastName() == null || req.getLastName().isBlank()) {
            throw new IllegalArgumentException("Prenumele si numele inspectorului sunt obligatorii");
        }

        UUID id = appService.creeazaInspector(req.getFirstName(), req.getLastName());
        return new ResponseEntity<>(new InspectorCreatResponse(id), HttpStatus.CREATED);
    }

    @PostMapping("/inspectori/{id}/certificari")
    @Operation(summary = "Adauga certificare profesionala inspectorului")
    public ResponseEntity<Void> adaugaCertificare(@PathVariable UUID id,
                                                 @RequestParam String denumire,
                                                 @RequestParam String dataExpirare) {
        appService.adaugaCertificareInspector(id, denumire, LocalDate.parse(dataExpirare));
        return ResponseEntity.ok().build();
    }

    @PostMapping("/inspectori/{id}/disponibilitate")
    @Operation(summary = "Declara intervalul de disponibilitate al inspectorului")
    public ResponseEntity<Void> declaraDisponibilitate(@PathVariable UUID id,
                                                      @RequestParam String deLa,
                                                      @RequestParam String panaLa) {
        appService.declaraDisponibilitateInspector(id, LocalDate.parse(deLa), LocalDate.parse(panaLa));
        return ResponseEntity.ok().build();
    }

    @PostMapping("/inspectii/{id}/alocare")
    @Operation(summary = "Aloca un inspector unei inspectii programate")
    public ResponseEntity<Void> alocaInspector(@PathVariable UUID id, @RequestParam UUID inspectorId) {
        appService.alocaInspector(new AlocaInspectorCommand(id, inspectorId));
        return ResponseEntity.ok().build();
    }

    @PostMapping("/inspectii/{id}/start")
    @Operation(summary = "Incepe inspectia de calitate")
    public ResponseEntity<Void> incepeInspectie(@PathVariable UUID id) {
        appService.incepeInspectie(new IncepeInspectieCommand(id));
        return ResponseEntity.ok().build();
    }

    @PostMapping(value = "/inspectii/{id}/verdict", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Inregistreaza verdictul inspectiei (JSON payload)")
    public ResponseEntity<Void> inregistreazaVerdictJson(@PathVariable UUID id, @Valid @RequestBody InregistreazaVerdictRequest req) {
        appService.inregistreazaVerdict(new InregistreazaVerdictCommand(id, req.getDefecteGasite()));
        return ResponseEntity.ok().build();
    }

    @PostMapping(value = "/inspectii/{id}/verdict")
    @Operation(summary = "Inregistreaza verdictul inspectiei (Form payload)")
    public ResponseEntity<Void> inregistreazaVerdictForm(@PathVariable UUID id, @Valid @ModelAttribute InregistreazaVerdictRequest req) {
        appService.inregistreazaVerdict(new InregistreazaVerdictCommand(id, req.getDefecteGasite()));
        return ResponseEntity.ok().build();
    }

    @PostMapping("/inspectii/{id}/reinspectie")
    @Operation(summary = "Solicita o reinspectie de calitate")
    public ResponseEntity<Void> solicitaReinspectie(@PathVariable UUID id) {
        appService.solicitaReinspectie(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/ncr/{id}/cauza")
    @Operation(summary = "Inregistreaza analiza cauzei radacina a non-conformitatii")
    public ResponseEntity<Void> analizeazaCauzaNCR(@PathVariable UUID id, @RequestParam String cauza) {
        appService.analizeazaCauzaNCR(id, cauza);
        return ResponseEntity.ok().build();
    }

    @PostMapping(value = "/ncr/{id}/actiune", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Initiaza o actiune corectiva/preventiva CAPA (JSON payload)")
    public ResponseEntity<Void> initiazaActiuneNCRJson(@PathVariable UUID id, @Valid @RequestBody InitiazaActiuneNCRRequest req) {
        return processInitiazaActiuneNCR(id, req);
    }

    @PostMapping(value = "/ncr/{id}/actiune")
    @Operation(summary = "Initiaza o actiune corectiva/preventiva CAPA (Form payload)")
    public ResponseEntity<Void> initiazaActiuneNCRForm(@PathVariable UUID id, @Valid @ModelAttribute InitiazaActiuneNCRRequest req) {
        return processInitiazaActiuneNCR(id, req);
    }

    private ResponseEntity<Void> processInitiazaActiuneNCR(UUID id, InitiazaActiuneNCRRequest req) {
        if (req.getDescriere() == null || req.getResponsabil() == null || req.getTermen() == null) {
            throw new IllegalArgumentException("Descrierea, responsabilul si termenul sunt obligatorii");
        }
        appService.initiazaActiuneNCR(id, req.getDescriere(), req.getResponsabil(), LocalDate.parse(req.getTermen()));
        return ResponseEntity.ok().build();
    }

    @PostMapping("/ncr/{id}/verificare")
    @Operation(summary = "Verifica eficacitatea actiunii NCR")
    public ResponseEntity<Void> verificaNCR(@PathVariable UUID id) {
        appService.verificaNCR(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/ncr/{id}/inchidere")
    @Operation(summary = "Inchide raportul de non-conformitate")
    public ResponseEntity<Void> inchideNCR(@PathVariable UUID id) {
        appService.inchideNCR(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/ncr/{id}/escaladare")
    @Operation(summary = "Escaladeaza raportul de non-conformitate")
    public ResponseEntity<Void> escaladeazaNCR(@PathVariable UUID id) {
        appService.escaladeazaNCR(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping(value = "/furnizori/{id}/scorecard-lunar", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Calculeaza scorecardul lunar pentru un furnizor (JSON payload)")
    public ResponseEntity<Void> calculeazaScorecardJson(@PathVariable UUID id, @Valid @RequestBody CalculScorecardRequest req) {
        return processCalculScorecard(id, req);
    }

    @PostMapping(value = "/furnizori/{id}/scorecard-lunar")
    @Operation(summary = "Calculeaza scorecardul lunar pentru un furnizor (Form payload)")
    public ResponseEntity<Void> calculeazaScorecardForm(@PathVariable UUID id, @Valid @ModelAttribute CalculScorecardRequest req) {
        return processCalculScorecard(id, req);
    }

    private ResponseEntity<Void> processCalculScorecard(UUID id, CalculScorecardRequest req) {
        if (req.getAn() <= 0 || req.getLuna() <= 0) {
            throw new IllegalArgumentException("Anul si luna sunt obligatorii");
        }
        LocalDate start = LocalDate.of(req.getAn(), req.getLuna(), 1);
        LocalDate end = start.plusMonths(1).minusDays(1);
        appService.calculeazaScorecardLunar(new CalculScorecardCommand(
            IdFurnizor.of(id), new Perioada(start, end), BigDecimal.valueOf(req.getRataRespingere())
        ));
        return ResponseEntity.ok().build();
    }

    @GetMapping("/calitate/dashboard")
    @Operation(summary = "Obtine tabloul de bord global pentru indicatorii de calitate")
    public ResponseEntity<CalitateDashboardView> obtineDashboardCalitate() {
        return ResponseEntity.ok(queryService.obtineDashboardCalitate());
    }

    @GetMapping("/furnizori/rapoarte/monitorizare")
    @Operation(summary = "Obtine raportul de monitorizare a furnizorilor")
    public ResponseEntity<List<MonitorizareFurnizoriView>> obtineMonitorizareFurnizori() {
        return ResponseEntity.ok(queryService.obtineMonitorizareFurnizori());
    }

    @GetMapping("/furnizori")
    @Operation(summary = "Listeaza furnizorii paginat")
    public ResponseEntity<List<FurnizorListItemView>> listeazaFurnizori(
            @RequestParam(required = false) String stare,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(queryService.listeazaFurnizori(stare, search, page, size));
    }

    @GetMapping("/furnizori/{id}")
    @Operation(summary = "Obtine detaliile unui furnizor")
    public ResponseEntity<FurnizorDetaliiView> obtineDetaliiFurnizor(@PathVariable UUID id) {
        return queryService.obtineDetaliiFurnizor(IdFurnizor.of(id))
            .map(ResponseEntity::ok)
            .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/inspectii")
    @Operation(summary = "Listeaza inspectiile de calitate paginat")
    public ResponseEntity<List<InspectieListItemView>> listeazaInspectii(
            @RequestParam(required = false) UUID furnizorId,
            @RequestParam(required = false) UUID inspectorId,
            @RequestParam(required = false) String stare,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(queryService.listeazaInspectii(furnizorId, inspectorId, stare, page, size));
    }

    @GetMapping("/inspectii/{id}")
    @Operation(summary = "Obtine detaliile unei inspectii")
    public ResponseEntity<InspectieListItemView> obtineDetaliiInspectie(@PathVariable UUID id) {
        return queryService.obtineDetaliiInspectie(id)
            .map(ResponseEntity::ok)
            .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/inspectori")
    @Operation(summary = "Listeaza inspectorii de calitate")
    public ResponseEntity<List<InspectorListItemView>> listeazaInspectori() {
        return ResponseEntity.ok(queryService.listeazaInspectori());
    }

    @GetMapping("/ncr")
    @Operation(summary = "Listeaza rapoartele de non-conformitate paginat")
    public ResponseEntity<List<NCRListItemView>> listeazaNCR(
            @RequestParam(required = false) UUID furnizorId,
            @RequestParam(required = false) String stare,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(queryService.listeazaNCR(furnizorId, stare, page, size));
    }

    @GetMapping("/ncr/{id}")
    @Operation(summary = "Obtine detaliile unui raport de non-conformitate")
    public ResponseEntity<NCRListItemView> obtineDetaliiNCR(@PathVariable UUID id) {
        return queryService.obtineDetaliiNCR(id)
            .map(ResponseEntity::ok)
            .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
