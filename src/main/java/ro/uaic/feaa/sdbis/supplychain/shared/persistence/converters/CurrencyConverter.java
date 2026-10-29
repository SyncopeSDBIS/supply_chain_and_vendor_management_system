package ro.uaic.feaa.sdbis.supplychain.shared.persistence.converters;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.util.Currency;

@Converter(autoApply = true)
public class CurrencyConverter implements AttributeConverter<Currency, String> {

    @Override
    public String convertToDatabaseColumn(Currency attribute) {
        return attribute != null ? attribute.getCurrencyCode() : null;
    }

    @Override
    public Currency convertToEntityAttribute(String dbData) {
        return (dbData != null && !dbData.isBlank()) ? Currency.getInstance(dbData.trim()) : null;
    }
}

