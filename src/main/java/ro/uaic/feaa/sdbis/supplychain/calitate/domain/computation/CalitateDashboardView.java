package ro.uaic.feaa.sdbis.supplychain.calitate.domain.computation;

import ro.uaic.feaa.sdbis.supplychain.shared.annotation.ValueObject;

import java.io.Serializable;

/**
 * Entity View Computation: Proiectie dashboard pentru calitatea furnizorilor si inspectii (Sprint 2 - ADR-003).
 */
@ValueObject
public record CalitateDashboardView(
    int totalFurnizori,
    int furnizoriAprobati,
    int furnizoriBlocati,
    int totalInspectii,
    int inspectiiAdmise,
    int inspectiiRespinse,
    double rataConformitateLoturi
) implements Serializable {}
