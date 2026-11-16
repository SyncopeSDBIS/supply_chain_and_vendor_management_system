package ro.uaic.feaa.sdbis.supplychain.facturi.presentation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ro.uaic.feaa.sdbis.supplychain.facturi.application.FacturaApplicationService;
import ro.uaic.feaa.sdbis.supplychain.facturi.application.StripeWebhookProcessingService;
import ro.uaic.feaa.sdbis.supplychain.facturi.application.command.CreeazaOrdinPlataCommand;
import ro.uaic.feaa.sdbis.supplychain.facturi.application.dto.WebhookResponseDTO;
import ro.uaic.feaa.sdbis.supplychain.facturi.application.command.InregistreazaFacturaCommand;
import ro.uaic.feaa.sdbis.supplychain.facturi.application.command.ReconciliereCommand;
import ro.uaic.feaa.sdbis.supplychain.facturi.application.command.TransmiteOrdinCommand;
import ro.uaic.feaa.sdbis.supplychain.facturi.application.query.FacturaDetaliiView;
import ro.uaic.feaa.sdbis.supplychain.facturi.application.query.FacturaListItemView;
import ro.uaic.feaa.sdbis.supplychain.facturi.application.query.FacturaQueryService;
import ro.uaic.feaa.sdbis.supplychain.facturi.application.query.NIRListItemView;
import ro.uaic.feaa.sdbis.supplychain.facturi.application.query.OrdinPlataListItemView;
import ro.uaic.feaa.sdbis.supplychain.facturi.application.query.ScadentarFacturiView;
import ro.uaic.feaa.sdbis.supplychain.facturi.domain.LinieFactura;
import ro.uaic.feaa.sdbis.supplychain.facturi.domain.Suma;
import ro.uaic.feaa.sdbis.supplychain.shared.CodMaterial;
import ro.uaic.feaa.sdbis.supplychain.shared.IdComanda;
import ro.uaic.feaa.sdbis.supplychain.shared.IdFurnizor;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Currency;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@Validated
@RequestMapping("/api")
@Tag(name = "Facturi", description = "Management facturi furnizor, receptii NIR, reconciliere 3-Way Matching si plati")
public class FacturaCtrl {

    private final FacturaApplicationService appService;
    private final FacturaQueryService queryService;
    private final StripeWebhookProcessingService webhookProcessingService;

    public FacturaCtrl(FacturaApplicationService appService,
                       FacturaQueryService queryService,
                       StripeWebhookProcessingService webhookProcessingService) {
        this.appService = appService;
        this.queryService = queryService;
        this.webhookProcessingService = webhookProcessingService;
    }

    @PostMapping(value = "/facturi", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Inregistreaza o noua factura fiscala de furnizor (JSON payload)")
    public ResponseEntity<FacturaInregistrataResponse> inregistreazaFacturaJson(@Valid @RequestBody InregistreazaFacturaRequest req) {
        return processInregistreazaFactura(req);
    }

    @PostMapping(value = "/facturi")
    @Operation(summary = "Inregistreaza o noua factura fiscala de furnizor (Form / URL-encoded payload)")
    public ResponseEntity<FacturaInregistrataResponse> inregistreazaFacturaForm(@Valid @ModelAttribute InregistreazaFacturaRequest req) {
        return processInregistreazaFactura(req);
    }

    private ResponseEntity<FacturaInregistrataResponse> processInregistreazaFactura(InregistreazaFacturaRequest req) {
        if (req.getSerie() == null || req.getNumar() == null || req.getDataEmitere() == null
                || req.getDataScadenta() == null || req.getComandaId() == null || req.getFurnizorId() == null) {
            throw new IllegalArgumentException("Parametrii obligatorii lipsesc pentru inregistrarea facturii");
        }

        LinieFactura linie = new LinieFactura(
            CodMaterial.of(req.getMaterialCod()),
            BigDecimal.valueOf(req.getCantitate()),
            req.getUnitate(),
            Suma.of(req.getPretUnitar(), req.getMoneda()),
            BigDecimal.valueOf(req.getCotaTva() != null && req.getCotaTva() >= 0 ? req.getCotaTva() : 0.19)
        );

        UUID id = appService.inregistreazaFactura(new InregistreazaFacturaCommand(
            req.getSerie(), req.getNumar(), LocalDate.parse(req.getDataEmitere()), LocalDate.parse(req.getDataScadenta()),
            IdComanda.of(req.getComandaId()), IdFurnizor.of(req.getFurnizorId()),
            Collections.singletonList(linie), Currency.getInstance(req.getMoneda())
        ));

        return new ResponseEntity<>(new FacturaInregistrataResponse(id, "Inregistrata"), HttpStatus.CREATED);
    }

    @PostMapping("/facturi/{id}/contestare")
    @Operation(summary = "Inregistreaza o contestatie asupra facturii")
    public ResponseEntity<Void> contestaFactura(@PathVariable UUID id, @RequestParam String motiv) {
        appService.contestaFactura(id, motiv);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/facturi/{id}/reconciliere")
    @Operation(summary = "Executa reconcilierea 3-Way Matching intre Factura, Comanda si NIR")
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
    @Operation(summary = "Contabilizeaza receptia bunurilor din NIR")
    public ResponseEntity<Void> contabilizeazaNIR(@PathVariable UUID id) {
        appService.contabilizeazaNIR(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/nir/{id}/stornare")
    @Operation(summary = "Storneaza receptia bunurilor din NIR")
    public ResponseEntity<Void> storneazaNIR(@PathVariable UUID id, @RequestParam String motiv) {
        appService.storneazaNIR(id, motiv);
        return ResponseEntity.ok().build();
    }

    @PostMapping(value = "/facturi/{id}/ordine-plata", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Creeaza un ordin de plata pentru o factura aprobata (JSON payload)")
    public ResponseEntity<OrdinPlataCreatResponse> creeazaOrdinPlataJson(
            @PathVariable UUID id,
            @Valid @RequestBody CreeazaOrdinPlataRequest req) {
        return processCreeazaOrdinPlata(id, req);
    }

    @PostMapping(value = "/facturi/{id}/ordine-plata")
    @Operation(summary = "Creeaza un ordin de plata pentru o factura aprobata (Form payload)")
    public ResponseEntity<OrdinPlataCreatResponse> creeazaOrdinPlataForm(
            @PathVariable UUID id,
            @Valid @ModelAttribute CreeazaOrdinPlataRequest req) {
        return processCreeazaOrdinPlata(id, req);
    }

    private ResponseEntity<OrdinPlataCreatResponse> processCreeazaOrdinPlata(UUID id, CreeazaOrdinPlataRequest req) {
        if (req.getSuma() <= 0) {
            throw new IllegalArgumentException("Parametrii obligatorii lipsesc pentru crearea ordinului de plata");
        }

        // FIX SEC-02: Extrage identitatea creatorului din SecurityContextHolder pentru a preveni falsificarea Four-Eyes
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UUID creatorId = null;
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
            String name = auth.getName();
            if (name != null && !name.isBlank()) {
                try {
                    creatorId = UUID.fromString(name);
                } catch (IllegalArgumentException ignored) {
                    creatorId = UUID.nameUUIDFromBytes(name.getBytes(StandardCharsets.UTF_8));
                }
            }
        }
        if (creatorId == null) {
            creatorId = req.getCreatDe();
        }
        if (creatorId == null) {
            throw new IllegalArgumentException("Identitatea creatorului lipseste pentru crearea ordinului de plata");
        }

        UUID ordinId = appService.creeazaOrdinPlata(new CreeazaOrdinPlataCommand(id, Suma.of(req.getSuma(), req.getMoneda()), creatorId));
        return new ResponseEntity<>(new OrdinPlataCreatResponse(ordinId, id, "Creat"), HttpStatus.CREATED);
    }

    @PostMapping("/ordine-plata/{id}/aprobare")
    @Operation(summary = "Aproba ordinul de plata folosind identitatea din contextul de securitate (FIX SEC-02)")
    public ResponseEntity<Void> aprobaOrdin(@PathVariable UUID id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            throw new SecurityException("Autentificare obligatorie prin token Bearer JWT pentru aprobarea ordinului de plata.");
        }
        String name = auth.getName();
        if (name == null || name.isBlank()) {
            throw new SecurityException("Identitatea aprobatorului este obligatorie pentru aprobarea ordinului de plata.");
        }
        UUID callerId;
        try {
            callerId = UUID.fromString(name);
        } catch (IllegalArgumentException ignored) {
            callerId = UUID.nameUUIDFromBytes(name.getBytes(StandardCharsets.UTF_8));
        }
        appService.aprobaOrdin(id, callerId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/ordine-plata/{id}/transmitere")
    @Operation(summary = "Transmite ordinul de plata catre institutia bancara")
    public ResponseEntity<Void> transmiteOrdin(@PathVariable UUID id, @RequestParam String iban) {
        appService.transmiteOrdin(new TransmiteOrdinCommand(id, iban));
        return ResponseEntity.ok().build();
    }

    @PostMapping(value = {"/ordine-plata/webhook-banca", "/plati/webhook-banca"})
    @Operation(summary = "Proceseaza webhook-ul Stripe / institutie bancara pentru confirmarea platii (FIX REST-02)")
    public ResponseEntity<WebhookResponseDTO> webhookBanca(
            @RequestHeader(value = "Stripe-Signature", required = false) String sigHeader,
            @RequestBody(required = false) String payload,
            @RequestParam(required = false) UUID ordinId,
            @RequestParam(required = false, defaultValue = "payment_intent.succeeded") String eventType,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String motiv) {
        var result = webhookProcessingService.proceseazaWebhookBanca(sigHeader, payload, ordinId, eventType, status, motiv);
        return ResponseEntity.status(result.statusCode()).body(WebhookResponseDTO.fromResult(result));
    }

    @GetMapping("/facturi/scadente")
    @Operation(summary = "Obtine raportul scadentar al facturilor de plata")
    public ResponseEntity<List<ScadentarFacturiView>> obtineScadentar() {
        return ResponseEntity.ok(queryService.obtineScadentar());
    }

    @GetMapping("/facturi")
    @Operation(summary = "Listeaza facturile furnizorilor paginat")
    public ResponseEntity<List<FacturaListItemView>> listeazaFacturi(
            @RequestParam(required = false) UUID furnizorId,
            @RequestParam(required = false) UUID comandaId,
            @RequestParam(required = false) String stare,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(queryService.listeazaFacturi(furnizorId, comandaId, stare, page, size));
    }

    @GetMapping("/facturi/{id}")
    @Operation(summary = "Obtine detaliile complete ale unei facturi")
    public ResponseEntity<FacturaDetaliiView> obtineDetaliiFactura(@PathVariable UUID id) {
        return queryService.obtineDetaliiFactura(id)
            .map(ResponseEntity::ok)
            .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/nir")
    @Operation(summary = "Listeaza notele de intrare-receptie NIR")
    public ResponseEntity<List<NIRListItemView>> listeazaNIR(
            @RequestParam(required = false) UUID comandaId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(queryService.listeazaNIR(comandaId, page, size));
    }

    @GetMapping("/nir/{id}")
    @Operation(summary = "Obtine detaliile unui NIR")
    public ResponseEntity<NIRListItemView> obtineDetaliiNIR(@PathVariable UUID id) {
        return queryService.obtineDetaliiNIR(id)
            .map(ResponseEntity::ok)
            .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/ordine-plata")
    @Operation(summary = "Listeaza ordinele de plata")
    public ResponseEntity<List<OrdinPlataListItemView>> listeazaOrdinePlata(
            @RequestParam(required = false) UUID facturaId,
            @RequestParam(required = false) String stare,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(queryService.listeazaOrdinePlata(facturaId, stare, page, size));
    }

    @GetMapping("/ordine-plata/{id}")
    @Operation(summary = "Obtine detaliile unui ordin de plata")
    public ResponseEntity<OrdinPlataListItemView> obtineDetaliiOrdinPlata(@PathVariable UUID id) {
        return queryService.obtineDetaliiOrdinPlata(id)
            .map(ResponseEntity::ok)
            .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
