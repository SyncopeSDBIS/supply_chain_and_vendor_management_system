package ro.uaic.feaa.sdbis.supplychain.facturi.domain;

import ro.uaic.feaa.sdbis.supplychain.shared.annotation.ValueObject;

@ValueObject
public enum StareFactura {
    Inregistrata,
    InVerificare,
    Blocata,
    Contestata,
    AprobataLaPlata,
    PartialPlatita,
    Platita,
    Anulata
}
