package ro.uaic.feaa.sdbis.supplychain.comenzi.api.dto;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Public Contract DTO representing a line item in a purchase order.
 */
public record LinieComandaDTO(
        String material,
        BigDecimal cantitate,
        String unitateMasura,
        BigDecimal pretUnitar,
        String moneda
) implements Serializable {}
