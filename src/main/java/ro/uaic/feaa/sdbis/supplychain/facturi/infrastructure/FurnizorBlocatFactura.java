package ro.uaic.feaa.sdbis.supplychain.facturi.infrastructure;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "facturi_furnizori_blocati")
public class FurnizorBlocatFactura implements Serializable {

    @Id
    @Column(name = "furnizor_id", nullable = false)
    private UUID furnizorId;

    @Column(name = "data_blocare", nullable = false)
    private Instant dataBlocare;

    protected FurnizorBlocatFactura() {}

    public FurnizorBlocatFactura(UUID furnizorId) {
        this.furnizorId = Objects.requireNonNull(furnizorId);
        this.dataBlocare = Instant.now();
    }

    public UUID getFurnizorId() {
        return furnizorId;
    }

    public Instant getDataBlocare() {
        return dataBlocare;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FurnizorBlocatFactura that)) return false;
        return Objects.equals(furnizorId, that.furnizorId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(furnizorId);
    }
}

