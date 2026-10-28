package ro.uaic.feaa.sdbis.supplychain.calitate.domain;

import ro.uaic.feaa.sdbis.supplychain.shared.annotation.DomainService;

import java.util.Objects;

/**
 * Domain Service pentru validarea si alocarea inspectorului de calitate conform disponibilitatii si certificarii (Figura 20).
 */
@DomainService
public class AlocareInspectorService {

    public void aloca(InspectieCalitate inspectie, InspectorCalitate inspector) {
        Objects.requireNonNull(inspectie, "inspectie cannot be null");
        Objects.requireNonNull(inspector, "inspector cannot be null");

        if (!inspector.poateFiAlocat(inspectie.getPerioadaPlanificata())) {
            throw new IllegalStateException("Inspectorul " + inspector.getFullName() + " nu are certificare valida sau nu este disponibil in perioada planificata");
        }
        inspectie.aloca(inspector.getIdInspector());
    }
}
