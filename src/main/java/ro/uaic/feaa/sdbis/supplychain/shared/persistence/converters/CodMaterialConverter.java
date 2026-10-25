package ro.uaic.feaa.sdbis.supplychain.shared.persistence.converters;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import ro.uaic.feaa.sdbis.supplychain.shared.CodMaterial;

@Converter(autoApply = false)
public class CodMaterialConverter implements AttributeConverter<CodMaterial, String> {
    @Override
    public String convertToDatabaseColumn(CodMaterial attribute) {
        return attribute == null ? null : attribute.getCod();
    }
    @Override
    public CodMaterial convertToEntityAttribute(String dbData) {
        return dbData == null ? null : CodMaterial.of(dbData);
    }
}
