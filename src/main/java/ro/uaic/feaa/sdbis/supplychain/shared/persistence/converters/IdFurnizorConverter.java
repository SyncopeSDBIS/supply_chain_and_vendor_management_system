package ro.uaic.feaa.sdbis.supplychain.shared.persistence.converters;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import ro.uaic.feaa.sdbis.supplychain.shared.IdFurnizor;
import java.util.UUID;

@Converter(autoApply = false)
public class IdFurnizorConverter implements AttributeConverter<IdFurnizor, UUID> {
    @Override
    public UUID convertToDatabaseColumn(IdFurnizor attribute) {
        return attribute == null ? null : attribute.getValue();
    }
    @Override
    public IdFurnizor convertToEntityAttribute(UUID dbData) {
        return dbData == null ? null : IdFurnizor.of(dbData);
    }
}
