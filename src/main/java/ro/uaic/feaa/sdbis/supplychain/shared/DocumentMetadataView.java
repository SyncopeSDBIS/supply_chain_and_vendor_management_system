package ro.uaic.feaa.sdbis.supplychain.shared;

import java.time.Instant;
import java.util.UUID;

/**
 * Proiectie JPA (Read Model) de metadate document atasat.
 * Elimina incarcarea continutului binar (byte[]) la listari.
 */
public interface DocumentMetadataView {
    UUID getId();
    String getTipDocument();
    UUID getReferintaId();
    String getNumeFisier();
    String getTipContinut();
    long getDimensiuneBytes();
    Instant getDataIncarcare();
}

