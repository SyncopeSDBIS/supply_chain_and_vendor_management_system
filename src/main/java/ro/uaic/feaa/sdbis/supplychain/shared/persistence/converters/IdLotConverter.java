package ro.uaic.feaa.sdbis.supplychain.shared.persistence.converters;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import ro.uaic.feaa.sdbis.supplychain.shared.IdLot;
import java.util.UUID;

@Converter(autoApply = false)
public class IdLotConverter implements AttributeConverter<IdLot, UUID> {
    @Override
    public UUID convertToDatabaseColumn(IdLot attribute) {
        return attribute == null ? null : attribute.getValue();
    }
    @Override
    public IdLot convertToEntityAttribute(UUID dbData) {
        return dbData == null ? null : IdLot.of(dbData);
    }
}
