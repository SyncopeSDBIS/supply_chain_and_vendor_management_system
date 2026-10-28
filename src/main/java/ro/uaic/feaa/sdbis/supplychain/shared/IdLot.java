package ro.uaic.feaa.sdbis.supplychain.shared;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import ro.uaic.feaa.sdbis.supplychain.shared.annotation.ValueObject;
import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@ValueObject
@Embeddable
public final class IdLot implements Serializable {
    @Column(name = "id_val")
    private UUID value;

    protected IdLot() {}

    public IdLot(UUID value) {
        this.value = Objects.requireNonNull(value, "IdLot value cannot be null");
    }

    public static IdLot of(UUID value) {
        return new IdLot(value);
    }

    public static IdLot random() {
        return new IdLot(UUID.randomUUID());
    }

    public UUID getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof IdLot that)) return false;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return value != null ? value.toString() : "";
    }
}
