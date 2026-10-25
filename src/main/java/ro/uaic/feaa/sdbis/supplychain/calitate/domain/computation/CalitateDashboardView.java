package ro.uaic.feaa.sdbis.supplychain.calitate.domain.computation;

import java.io.Serializable;

/**
 * Entity View Computation: Proiectie dashboard pentru calitatea furnizorilor si inspectii (Sprint 2 - ADR-003).
 */
public record CalitateDashboardView(
    int totalFurnizori,
    int furnizoriAprobati,
    int furnizoriBlocati,
    int totalInspectii,
    int inspectiiAdmise,
    int inspectiiRespinse,
    double rataConformitateLoturi
) implements Serializable {}
