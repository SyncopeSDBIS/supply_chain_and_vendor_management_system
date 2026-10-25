package ro.uaic.feaa.sdbis.supplychain.shared;

import java.util.UUID;

/**
 * Port pentru verificarea idempotentei la consumul evenimentelor (Idempotent Inbox pattern).
 * Suporta urmarirea per-consumer a procesarii fiecarui eveniment.
 */
public interface InboxRepo {
    boolean existaEveniment(UUID eventId);
    void marcheazaProcesat(UUID eventId);

    boolean existaEveniment(String consumer, UUID eventId);
    void marcheazaProcesat(String consumer, UUID eventId);
}
