package ro.uaic.feaa.sdbis.supplychain.comenzi.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import java.time.LocalDate;

/**
 * Request payload structurat pentru adaugarea/planificarea unui lot nou.
 */
public record PlanificaLotRequest(
    @NotBlank(message = "Codul lotului este obligatoriu")
    String codLot,

    @NotBlank(message = "Codul materialului este obligatoriu")
    String materialCod,

    @Positive(message = "Cantitatea trebuie sa fie strict pozitiva")
    double cantitate,

    @NotBlank(message = "Unitatea de masura este obligatorie")
    String unitate,

    LocalDate dataProductie,
    LocalDate dataExpirare
) {}
