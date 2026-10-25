package ro.uaic.feaa.sdbis.supplychain.calitate.infrastructure;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ro.uaic.feaa.sdbis.supplychain.calitate.application.CalitateApplicationService;
import ro.uaic.feaa.sdbis.supplychain.shared.IdFurnizor;
import java.util.UUID;

/**
 * REST Endpoint (Open Host Service) pentru interogarea eligibilitatii furnizorului (Figura 12 si 20).
 */
@RestController
@RequestMapping("/api/furnizori")
public class FurnizoriApiRest {

    private final CalitateApplicationService appService;

    public FurnizoriApiRest(CalitateApplicationService appService) {
        this.appService = appService;
    }

    @GetMapping(value = "/{id}/eligibil", produces = {"application/json", "application/xml"})
    public ResponseEntity<Boolean> esteFurnizorEligibil(@PathVariable UUID id) {
        boolean eligibil = appService.esteFurnizorEligibil(IdFurnizor.of(id));
        return ResponseEntity.ok(eligibil);
    }
}
