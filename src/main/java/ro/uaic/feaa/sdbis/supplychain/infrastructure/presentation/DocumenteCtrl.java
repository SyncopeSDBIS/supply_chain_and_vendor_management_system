package ro.uaic.feaa.sdbis.supplychain.infrastructure.presentation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import ro.uaic.feaa.sdbis.supplychain.shared.DocumentAtasatEntity;
import ro.uaic.feaa.sdbis.supplychain.shared.DocumentStorageService;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Controller REST securizat pentru incarcarea si descarcarea documentelor fizice atasate fluxurilor P2P (DMS).
 * Implementeaza protectii DevSecOps impotriva Heap Exhaustion, Path Traversal si CRLF Injection (FIX SEC-F02).
 */
@RestController
@RequestMapping("/api/documente")
@Tag(name = "Documente", description = "Management documente electronice atasate fluxurilor P2P (DMS)")
public class DocumenteCtrl {

    private static final long MAX_FILE_SIZE_BYTES = 15L * 1024 * 1024; // 15 MB
    private static final Set<String> EXTENSII_PERMISE = Set.of("pdf", "png", "jpg", "jpeg", "docx", "xml");

    private final DocumentStorageService documentStorageService;

    public DocumenteCtrl(DocumentStorageService documentStorageService) {
        this.documentStorageService = documentStorageService;
    }

    @PostMapping(value = "/incarca", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Incarca un document atasat unei entitati de business")
    public ResponseEntity<DocumentMetadataResponse> incarcaDocument(
            @RequestParam("fisier") MultipartFile fisier,
            @RequestParam("tipDocument") String tipDocument,
            @RequestParam("referintaId") UUID referintaId) {

        if (fisier == null || fisier.isEmpty()) {
            throw new IllegalArgumentException("Fisierul incarcat este gol");
        }

        // 1. Validare dimensiune maxima (protectie DoS / Heap Exhaustion)
        if (fisier.getSize() > MAX_FILE_SIZE_BYTES) {
            throw new IllegalArgumentException("Dimensiunea fisierului depaseste limita maxima admisa de 15 MB");
        }

        // 2. Sanitizare nume fisier (protectie Path Traversal si caractere de control)
        String originalFilename = fisier.getOriginalFilename() != null ? fisier.getOriginalFilename() : "document.bin";
        String baseName = Paths.get(originalFilename).getFileName().toString();
        String sanitizedFilename = baseName.replaceAll("[^a-zA-Z0-9._-]", "_");
        if (sanitizedFilename.isBlank()) {
            sanitizedFilename = "document_" + UUID.randomUUID() + ".bin";
        }

        // 3. Validare Whitelist extensie
        String extension = "";
        int dotIdx = sanitizedFilename.lastIndexOf('.');
        if (dotIdx >= 0 && dotIdx < sanitizedFilename.length() - 1) {
            extension = sanitizedFilename.substring(dotIdx + 1).toLowerCase();
        }
        if (!EXTENSII_PERMISE.contains(extension)) {
            throw new IllegalArgumentException("Tipul de fisier nu este permis. Extensii permise: " + EXTENSII_PERMISE);
        }

        try {
            String tipContinut = fisier.getContentType() != null ? fisier.getContentType() : MediaType.APPLICATION_OCTET_STREAM_VALUE;

            DocumentAtasatEntity doc = documentStorageService.salveazaDocument(
                    tipDocument,
                    referintaId,
                    sanitizedFilename,
                    tipContinut,
                    fisier.getBytes()
            );

            return ResponseEntity.status(HttpStatus.CREATED).body(new DocumentMetadataResponse(
                    doc.getId(),
                    doc.getTipDocument(),
                    doc.getReferintaId(),
                    doc.getNumeFisier(),
                    doc.getTipContinut(),
                    doc.getDimensiuneBytes(),
                    doc.getDataIncarcare()
            ));
        } catch (IOException e) {
            throw new IllegalStateException("Eroare la citirea continutului fisierului incarcat: " + e.getMessage(), e);
        }
    }

    @GetMapping("/{id}/descarca")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Descarca fisierul binar al documentului atasat")
    public ResponseEntity<byte[]> descarcaDocument(@PathVariable UUID id) {
        return documentStorageService.gasesteDupaId(id)
                .map(doc -> {
                    ContentDisposition contentDisposition = ContentDisposition.attachment()
                            .filename(doc.getNumeFisier(), StandardCharsets.UTF_8)
                            .build();
                    return ResponseEntity.ok()
                            .headers(headers -> headers.setContentDisposition(contentDisposition))
                            .contentType(MediaType.parseMediaType(doc.getTipContinut()))
                            .body(doc.getContinut());
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/referinta/{referintaId}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Listeaza metadatele documentelor atasate pentru o referinta de business")
    public ResponseEntity<List<DocumentMetadataResponse>> gasesteDocumenteReferinta(@PathVariable UUID referintaId) {
        var docList = documentStorageService.listeazaMetadateDupaReferinta(referintaId);
        List<DocumentMetadataResponse> result = docList.stream().map(doc -> new DocumentMetadataResponse(
                doc.getId(),
                doc.getTipDocument(),
                doc.getReferintaId(),
                doc.getNumeFisier(),
                doc.getTipContinut(),
                doc.getDimensiuneBytes(),
                doc.getDataIncarcare()
        )).toList();
        return ResponseEntity.ok(result);
    }
}
