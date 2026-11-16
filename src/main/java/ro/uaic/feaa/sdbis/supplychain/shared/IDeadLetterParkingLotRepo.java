package ro.uaic.feaa.sdbis.supplychain.shared;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Port pentru administrarea si persistenta mesajelor esuate din Dead-Letter Queue (AMQP-AUDIT-06).
 */
public interface IDeadLetterParkingLotRepo {
    void salveazaMesajEsuat(DeadLetterMessageEntity entity);
    Optional<DeadLetterMessageEntity> gasesteDupaId(UUID id);
    List<DeadLetterMessageEntity> gasesteToateNeprocesate();
}
