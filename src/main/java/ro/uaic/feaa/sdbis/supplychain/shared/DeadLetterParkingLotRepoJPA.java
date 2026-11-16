package ro.uaic.feaa.sdbis.supplychain.shared;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class DeadLetterParkingLotRepoJPA extends AbstractRepositoryJPA<DeadLetterMessageEntity, UUID> implements IDeadLetterParkingLotRepo {

    public DeadLetterParkingLotRepoJPA() {
        super(DeadLetterMessageEntity.class);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void salveazaMesajEsuat(DeadLetterMessageEntity entity) {
        if (entity != null) {
            em.persist(entity);
            em.flush();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<DeadLetterMessageEntity> gasesteDupaId(UUID id) {
        if (id == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(em.find(DeadLetterMessageEntity.class, id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<DeadLetterMessageEntity> gasesteToateNeprocesate() {
        return em.createQuery("SELECT d FROM DeadLetterMessageEntity d WHERE d.retried = false ORDER BY d.failedAt DESC",
                DeadLetterMessageEntity.class).getResultList();
    }
}
