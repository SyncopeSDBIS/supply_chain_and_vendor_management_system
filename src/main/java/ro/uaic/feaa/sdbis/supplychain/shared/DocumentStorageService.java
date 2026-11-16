package ro.uaic.feaa.sdbis.supplychain.shared;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Serviciu de gestiune a documentelor si atasamentelor (DMS) din cadrul fluxurilor P2P.
 */
@Service
public class DocumentStorageService {

    private static final Logger log = LoggerFactory.getLogger(DocumentStorageService.class);

    private final DocumentStorageRepository repo;

    public DocumentStorageService(DocumentStorageRepository repo) {
        this.repo = repo;
    }

    @Transactional
    public DocumentAtasatEntity salveazaDocument(String tipDocument, UUID referintaId, String numeFisier, String tipContinut, byte[] continut) {
        UUID id = UUID.randomUUID();
        DocumentAtasatEntity doc = new DocumentAtasatEntity(id, tipDocument, referintaId, numeFisier, tipContinut, continut);
        DocumentAtasatEntity saved = repo.save(doc);
        log.info("[DMS] Document atasat salvat cu succes: {} (Tip: {}, Ref: {}, Marime: {} bytes)",
                id, tipDocument, referintaId, doc.getDimensiuneBytes());
        return saved;
    }

    @Transactional(readOnly = true)
    public Optional<DocumentAtasatEntity> gasesteDupaId(UUID id) {
        return repo.findById(id);
    }

    @Transactional(readOnly = true)
    public List<DocumentAtasatEntity> gasesteDupaReferinta(UUID referintaId) {
        return repo.findByReferintaIdOrderByDataIncarcareDesc(referintaId);
    }

    @Transactional(readOnly = true)
    public List<DocumentMetadataView> listeazaMetadateDupaReferinta(UUID referintaId) {
        return repo.findMetadataByReferintaIdOrderByDataIncarcareDesc(referintaId);
    }

    @Transactional
    public void stergeDocument(UUID id) {
        repo.deleteById(id);
        log.info("[DMS] Documentul {} a fost sters", id);
    }
}

