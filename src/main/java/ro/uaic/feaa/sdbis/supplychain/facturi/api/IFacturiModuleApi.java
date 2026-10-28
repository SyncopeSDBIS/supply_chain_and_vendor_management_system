package ro.uaic.feaa.sdbis.supplychain.facturi.api;

import ro.uaic.feaa.sdbis.supplychain.facturi.api.dto.FacturaSummaryDTO;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

/**
 * Public Contract & Entry Point for the Facturi (Invoicing & Payments) Bounded Context.
 * Other modules must interact with Facturi EXCLUSIVELY through this interface.
 */
public interface IFacturiModuleApi {

    void reconciliazaFactura(UUID facturaId);

    UUID initiazaPlata(UUID facturaId, BigDecimal suma, String moneda, UUID creatDe);

    Optional<FacturaSummaryDTO> obtineDetaliiFactura(UUID facturaId);

    void anuleazaFacturiPentruComanda(UUID comandaId);

    int proceseazaVerificareScadenteFacturi();
}
