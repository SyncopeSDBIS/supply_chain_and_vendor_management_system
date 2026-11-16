package ro.uaic.feaa.sdbis.supplychain.calitate.application.query;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record InspectorListItemView(
        UUID id,
        String numeComplet,
        List<CertificareView> certificari,
        List<DisponibilitateView> perioadeDisponibile
) implements Serializable {
    public record CertificareView(String denumire, LocalDate dataExpirare) implements Serializable {}
    public record DisponibilitateView(LocalDate deLa, LocalDate panaLa) implements Serializable {}
}

