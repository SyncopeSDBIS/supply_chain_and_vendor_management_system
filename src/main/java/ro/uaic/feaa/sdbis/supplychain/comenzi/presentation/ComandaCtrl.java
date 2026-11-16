package ro.uaic.feaa.sdbis.supplychain.comenzi.presentation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ro.uaic.feaa.sdbis.supplychain.comenzi.application.ComandaApplicationService;
import ro.uaic.feaa.sdbis.supplychain.comenzi.application.ComandaCommandFactory;
import ro.uaic.feaa.sdbis.supplychain.comenzi.application.command.AnuleazaComandaCommand;
import ro.uaic.feaa.sdbis.supplychain.comenzi.application.command.ConfirmaComandaCommand;
import ro.uaic.feaa.sdbis.supplychain.comenzi.application.command.CreeazaComandaCommand;
import ro.uaic.feaa.sdbis.supplychain.comenzi.application.command.InregistreazaExpediereCommand;
import ro.uaic.feaa.sdbis.supplychain.comenzi.application.command.InregistreazaReceptieCommand;
import ro.uaic.feaa.sdbis.supplychain.comenzi.application.dto.ActualizareComandaRequest;
import ro.uaic.feaa.sdbis.supplychain.comenzi.application.dto.ComandaSumarViewDTO;
import ro.uaic.feaa.sdbis.supplychain.comenzi.application.dto.CreeazaComandaRequest;
import ro.uaic.feaa.sdbis.supplychain.comenzi.application.dto.InregistreazaExpediereRequest;
import ro.uaic.feaa.sdbis.supplychain.comenzi.application.dto.InregistreazaReceptieRequest;
import ro.uaic.feaa.sdbis.supplychain.comenzi.application.dto.PerformantaLivrariSumarDTO;
import ro.uaic.feaa.sdbis.supplychain.comenzi.application.dto.PlanificaLotRequest;
import ro.uaic.feaa.sdbis.supplychain.comenzi.application.query.ComandaDetaliiView;
import ro.uaic.feaa.sdbis.supplychain.comenzi.application.query.ComandaListItemView;
import ro.uaic.feaa.sdbis.supplychain.comenzi.application.query.ComandaQueryService;
import ro.uaic.feaa.sdbis.supplychain.comenzi.application.query.DashboardLivrariView;
import ro.uaic.feaa.sdbis.supplychain.comenzi.application.query.TrasabilitateLotView;
import ro.uaic.feaa.sdbis.supplychain.comenzi.domain.Cantitate;
import ro.uaic.feaa.sdbis.supplychain.comenzi.domain.RezultatCalitateLot;
import ro.uaic.feaa.sdbis.supplychain.comenzi.domain.policy.PoliticaAprobareComanda;
import ro.uaic.feaa.sdbis.supplychain.comenzi.application.security.IApprovalAuthorityService;
import ro.uaic.feaa.sdbis.supplychain.shared.CodMaterial;
import ro.uaic.feaa.sdbis.supplychain.shared.IdComanda;
import ro.uaic.feaa.sdbis.supplychain.shared.IdFurnizor;
import ro.uaic.feaa.sdbis.supplychain.shared.IdLot;
import ro.uaic.feaa.sdbis.supplychain.shared.Perioada;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@RestController
@Validated
@RequestMapping("/api/comenzi")
@Tag(name = "Comenzi", description = "Management comenzi de aprovizionare, planificare loturi si receptie bunuri")
public class ComandaCtrl {

    private final ComandaApplicationService appService;
    private final ComandaQueryService queryService;
    private final IApprovalAuthorityService authorityService;

    public ComandaCtrl(ComandaApplicationService appService,
                       ComandaQueryService queryService,
                       IApprovalAuthorityService authorityService) {
        this.appService = appService;
        this.queryService = queryService;
        this.authorityService = authorityService;
    }

    @PostMapping(consumes = {"application/json", "application/xml"}, produces = {"application/json", "application/xml"})
    @Operation(summary = "Creeaza o comanda de aprovizionare (FIX SEC-03)")
    public ResponseEntity<ComandaCreatedResponse> creeazaComanda(@Valid @RequestBody CreeazaComandaRequest req) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UUID buyerId = null;
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
            boolean isAdmin = auth.getAuthorities().stream().anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()));
            if (!isAdmin || req.getCumparatorId() == null) {
                String name = auth.getName();
                if (name != null && !name.isBlank()) {
                    try {
                        buyerId = UUID.fromString(name);
                    } catch (IllegalArgumentException ignored) {
                        buyerId = UUID.nameUUIDFromBytes(name.getBytes(StandardCharsets.UTF_8));
                    }
                }
            }
        }
        if (buyerId == null) {
            buyerId = req.getCumparatorId();
        }
        CreeazaComandaCommand cmd = ComandaCommandFactory.fromRequest(req, buyerId);
        IdComanda id = appService.creeazaComanda(cmd);
        return new ResponseEntity<>(new ComandaCreatedResponse(id.getValue(), req.getNumarComanda(), "Solicitat"), HttpStatus.CREATED);
    }

    @GetMapping
    @Operation(summary = "Listeaza comenzile de aprovizionare paginat")
    public ResponseEntity<List<ComandaListItemView>> listeazaComenzi(
            @RequestParam(required = false) UUID furnizorId,
            @RequestParam(required = false) String stare,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(queryService.listeazaComenzi(furnizorId, stare, page, size));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtine detaliile complete ale unei comenzi")
    public ResponseEntity<ComandaDetaliiView> obtineDetaliiComanda(@PathVariable UUID id) {
        return queryService.obtineDetaliiComanda(IdComanda.of(id))
            .map(ResponseEntity::ok)
            .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping(value = "/{id}/sumar", produces = { "application/json", "application/xml" })
    @Operation(summary = "Obtine sumarul comenzii (Open Host Service)")
    public ResponseEntity<ComandaSumarViewDTO> obtineSumarComanda(@PathVariable UUID id) {
        ComandaSumarViewDTO dto = appService.obtineSumarComanda(IdComanda.of(id));
        return ResponseEntity.ok(dto);
    }

    @GetMapping(value = "/performanta", produces = { "application/json", "application/xml" })
    @Operation(summary = "Obtine performanta de livrare a furnizorului (Open Host Service)")
    public ResponseEntity<PerformantaLivrariSumarDTO> obtinePerformanta(
            @RequestParam UUID furnizorId,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM") YearMonth luna) {
        YearMonth targetPeriod = (luna != null) ? luna : YearMonth.now();
        Perioada perioada = Perioada.ofLuna(targetPeriod.getYear(), targetPeriod.getMonthValue());
        PerformantaLivrariSumarDTO dto = appService.obtinePerformanta(IdFurnizor.of(furnizorId), perioada);
        return ResponseEntity.ok(dto);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Editeaza informatiile generale ale comenzii (data livrare ceruta, depozit)")
    public ResponseEntity<Void> editeazaComanda(
            @PathVariable UUID id,
            @RequestBody(required = false) ActualizareComandaRequest body,
            @RequestParam(required = false) String dataLivrareCeruta,
            @RequestParam(required = false) String depozit) {
        LocalDate dt = null;
        if (body != null && body.dataLivrareCeruta() != null) {
            dt = body.dataLivrareCeruta();
        } else if (dataLivrareCeruta != null && !dataLivrareCeruta.isBlank()) {
            dt = LocalDate.parse(dataLivrareCeruta);
        }

        String dep = (body != null && body.depozit() != null) ? body.depozit() : depozit;
        appService.editeazaComanda(IdComanda.of(id), dt, dep);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Sterge o comanda aflata in stare incipienta")
    public ResponseEntity<Void> stergeComanda(@PathVariable UUID id) {
        appService.stergeComanda(IdComanda.of(id));
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/aprobare")
    @Operation(summary = "Aproba o comanda conform politicii de praguri valorice pe baza tokenului JWT")
    public ResponseEntity<Void> aprobaComanda(@PathVariable UUID id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            throw new SecurityException("Autentificare obligatorie prin token Bearer JWT pentru aprobarea comenzilor.");
        }

        UUID utilizatorId = authorityService.extrageUtilizatorId(authentication);
        PoliticaAprobareComanda.NivelAprobare nivel = authorityService.determinaNivelAprobare(authentication.getAuthorities());
        if (nivel == null) {
            throw new SecurityException("Utilizatorul nu detine roluri cu autoritate de aprobare in modulul comenzi.");
        }

        appService.valideazaSiAproba(IdComanda.of(id), utilizatorId, nivel);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/confirmare")
    @Operation(summary = "Inregistreaza confirmarea comenzii de catre furnizor cu data promisa")
    public ResponseEntity<Void> confirmaComanda(@PathVariable UUID id, @RequestParam String dataPromisa) {
        appService.confirmaComanda(new ConfirmaComandaCommand(IdComanda.of(id), LocalDate.parse(dataPromisa)));
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/refuz")
    @Operation(summary = "Inregistreaza refuzul comenzii de catre furnizor")
    public ResponseEntity<Void> refuzaComanda(@PathVariable UUID id) {
        appService.refuzaComanda(IdComanda.of(id));
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/expedieri")
    @Operation(summary = "Inregistreaza o expediere cu AWB si asociere de loturi")
    public ResponseEntity<Void> inregistreazaExpediere(
            @PathVariable UUID id,
            @RequestBody(required = false) InregistreazaExpediereRequest body,
            @RequestParam(required = false) String transportator,
            @RequestParam(required = false) String awb,
            @RequestParam(required = false) Set<UUID> loturiIds) {
        String finalTransportator = (body != null && body.transportator() != null) ? body.transportator() : transportator;
        String finalAwb = (body != null && body.awb() != null) ? body.awb() : awb;
        Set<UUID> finalLotIds = (body != null && body.loturiIds() != null) ? body.loturiIds() : loturiIds;

        Set<IdLot> loturi = new HashSet<>();
        if (finalLotIds != null) {
            for (UUID lid : finalLotIds) {
                loturi.add(IdLot.of(lid));
            }
        }
        appService.inregistreazaExpediere(new InregistreazaExpediereCommand(IdComanda.of(id), finalTransportator, finalAwb, loturi));
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/receptii")
    @Operation(summary = "Inregistreaza receptia fizica a unui lot")
    public ResponseEntity<Void> inregistreazaReceptie(
            @PathVariable UUID id,
            @RequestBody(required = false) InregistreazaReceptieRequest body,
            @RequestParam(required = false) UUID lotId,
            @RequestParam(required = false) Double cantitate,
            @RequestParam(required = false) String unitate) {
        UUID finalLotId = (body != null && body.lotId() != null) ? body.lotId() : lotId;
        double finalCantitate = (body != null && body.cantitate() > 0) ? body.cantitate() : (cantitate != null ? cantitate : 0.0);
        String finalUnitate = (body != null && body.unitate() != null) ? body.unitate() : unitate;

        appService.inregistreazaReceptie(new InregistreazaReceptieCommand(
            IdComanda.of(id), IdLot.of(finalLotId), Cantitate.of(finalCantitate, finalUnitate)
        ));
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/anulare")
    @Operation(summary = "Anuleaza o comanda de aprovizionare")
    public ResponseEntity<Void> anuleazaComanda(
            @PathVariable UUID id,
            @RequestParam(required = false) String motiv) {
        appService.anuleazaComanda(new AnuleazaComandaCommand(IdComanda.of(id), motiv));
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/inchidere")
    @Operation(summary = "Inchide definitiv o comanda complet receptionata si verificata calitativ")
    public ResponseEntity<Void> inchideComanda(@PathVariable UUID id) {
        appService.inchideComanda(IdComanda.of(id));
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/estimare-livrare")
    @Operation(summary = "Recalculeaza estimarea datei de livrare pe baza istoricului")
    public ResponseEntity<Void> estimeazaDataLivrare(@PathVariable UUID id) {
        appService.estimeazaDataLivrare(IdComanda.of(id));
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/loturi")
    @Operation(summary = "Planifica un lot de productie asociat comenzii")
    public ResponseEntity<LotPlanificatResponse> planificaLot(
            @PathVariable UUID id,
            @RequestBody(required = false) PlanificaLotRequest body,
            @RequestParam(required = false) String codLot,
            @RequestParam(required = false) String materialCod,
            @RequestParam(required = false) Double cantitate,
            @RequestParam(required = false) String unitate,
            @RequestParam(required = false) String dataProductie,
            @RequestParam(required = false) String dataExpirare) {
        String finalCodLot = (body != null && body.codLot() != null) ? body.codLot() : codLot;
        String finalMaterialCod = (body != null && body.materialCod() != null) ? body.materialCod() : materialCod;
        double finalCantitate = (body != null && body.cantitate() > 0) ? body.cantitate() : (cantitate != null ? cantitate : 0.0);
        String finalUnitate = (body != null && body.unitate() != null) ? body.unitate() : unitate;

        LocalDate finalDataProd = null;
        if (body != null && body.dataProductie() != null) {
            finalDataProd = body.dataProductie();
        } else if (dataProductie != null && !dataProductie.isBlank()) {
            finalDataProd = LocalDate.parse(dataProductie);
        }

        LocalDate finalDataExp = null;
        if (body != null && body.dataExpirare() != null) {
            finalDataExp = body.dataExpirare();
        } else if (dataExpirare != null && !dataExpirare.isBlank()) {
            finalDataExp = LocalDate.parse(dataExpirare);
        }

        IdLot lotId = appService.planificaLot(
            IdComanda.of(id),
            finalCodLot,
            CodMaterial.of(finalMaterialCod),
            Cantitate.of(finalCantitate, finalUnitate),
            finalDataProd,
            finalDataExp
        );
        return new ResponseEntity<>(new LotPlanificatResponse(lotId.getValue(), finalCodLot), HttpStatus.CREATED);
    }

    @PostMapping("/{id}/loturi/{lotId}/carantina")
    @Operation(summary = "Plaseaza un lot receptionat in carantina de inspectie")
    public ResponseEntity<Void> carantinaLot(@PathVariable UUID id, @PathVariable UUID lotId) {
        appService.plaseazaLotInCarantina(IdComanda.of(id), IdLot.of(lotId));
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/loturi/{lotId}/verdict")
    @Operation(summary = "Aplica verdictul de calitate rezultat in urma inspectiei AQL")
    public ResponseEntity<Void> aplicaVerdictInspectie(
            @PathVariable UUID id,
            @PathVariable UUID lotId,
            @RequestParam String rezultat) {
        RezultatCalitateLot res = RezultatCalitateLot.valueOf(rezultat);
        appService.aplicaVerdictInspectie(IdComanda.of(id), IdLot.of(lotId), res);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/loturi/{lotId}/retur")
    @Operation(summary = "Returneaza un lot respins catre furnizor")
    public ResponseEntity<Void> returneazaLot(@PathVariable UUID id, @PathVariable UUID lotId) {
        appService.returneazaLotFurnizorului(IdComanda.of(id), IdLot.of(lotId));
        return ResponseEntity.ok().build();
    }

    @GetMapping("/loturi/{codLot}/trasabilitate")
    @Operation(summary = "Obtine arborele de trasabilitate pentru un lot dupa codul acestuia")
    public ResponseEntity<TrasabilitateLotView> obtineTrasabilitateLot(@PathVariable String codLot) {
        return queryService.obtineTrasabilitateLot(codLot)
            .map(ResponseEntity::ok)
            .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/rapoarte/otif")
    @Operation(summary = "Obtine tabloul de bord OTIF pentru performanta livrarilor")
    public ResponseEntity<List<DashboardLivrariView>> obtineDashboardLivrari(@RequestParam(required = false) UUID furnizorId) {
        return ResponseEntity.ok(queryService.obtineDashboardLivrari(furnizorId));
    }
}
