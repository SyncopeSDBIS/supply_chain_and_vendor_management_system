package ro.uaic.feaa.sdbis.supplychain.comenzi.infrastructure;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ro.uaic.feaa.sdbis.supplychain.comenzi.application.ComandaApplicationService;
import ro.uaic.feaa.sdbis.supplychain.comenzi.application.dto.ComandaSummaryDTO;
import ro.uaic.feaa.sdbis.supplychain.comenzi.application.dto.PerformantaLivrariDTO;
import ro.uaic.feaa.sdbis.supplychain.shared.IdComanda;
import ro.uaic.feaa.sdbis.supplychain.shared.IdFurnizor;
import ro.uaic.feaa.sdbis.supplychain.shared.Perioada;
import java.util.UUID;

/**
 * REST Endpoint (Open Host Service) pentru integrare sincrona (Figura 14 si 19).
 */
@RestController
@RequestMapping("/api/comenzi")
public class ComenziApiRest {

    private final ComandaApplicationService appService;

    public ComenziApiRest(ComandaApplicationService appService) {
        this.appService = appService;
    }

    @GetMapping(value = "/{id}/sumar", produces = {"application/json", "application/xml"})
    public ResponseEntity<ComandaSummaryDTO> obtineSumarComanda(@PathVariable UUID id) {
        ComandaSummaryDTO dto = appService.obtineSumarComanda(IdComanda.of(id));
        return ResponseEntity.ok(dto);
    }

    @GetMapping(value = "/performanta", produces = {"application/json", "application/xml"})
    public ResponseEntity<PerformantaLivrariDTO> obtinePerformanta(@RequestParam UUID furnizorId) {
        PerformantaLivrariDTO dto = appService.obtinePerformanta(IdFurnizor.of(furnizorId), Perioada.ofLuna(2026, 3));
        return ResponseEntity.ok(dto);
    }
}
