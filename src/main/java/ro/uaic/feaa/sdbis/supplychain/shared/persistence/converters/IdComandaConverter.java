package ro.uaic.feaa.sdbis.supplychain.shared.persistence.converters;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import ro.uaic.feaa.sdbis.supplychain.shared.IdComanda;
import java.util.UUID;

@Converter(autoApply = false)
public class IdComandaConverter implements AttributeConverter<IdComanda, UUID> {
    @Override
    public UUID convertToDatabaseColumn(IdComanda attribute) {
        return attribute == null ? null : attribute.getValue();
    }
    @Override
    public IdComanda convertToEntityAttribute(UUID dbData) {
        return dbData == null ? null : IdComanda.of(dbData);
    }
}
