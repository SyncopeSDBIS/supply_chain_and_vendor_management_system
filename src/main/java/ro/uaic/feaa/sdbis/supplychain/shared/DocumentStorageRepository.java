package ro.uaic.feaa.sdbis.supplychain.shared;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface DocumentStorageRepository extends JpaRepository<DocumentAtasatEntity, UUID> {
    List<DocumentMetadataView> findMetadataByReferintaIdOrderByDataIncarcareDesc(UUID referintaId);
    List<DocumentMetadataView> findMetadataByTipDocumentOrderByDataIncarcareDesc(String tipDocument);
    List<DocumentAtasatEntity> findByReferintaIdOrderByDataIncarcareDesc(UUID referintaId);
    List<DocumentAtasatEntity> findByTipDocumentOrderByDataIncarcareDesc(String tipDocument);
}

