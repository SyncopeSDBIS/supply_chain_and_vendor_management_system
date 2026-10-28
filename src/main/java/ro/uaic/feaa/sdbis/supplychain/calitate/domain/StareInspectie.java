package ro.uaic.feaa.sdbis.supplychain.calitate.domain;

import ro.uaic.feaa.sdbis.supplychain.shared.annotation.ValueObject;

@ValueObject
public enum StareInspectie {
    Programata,
    InspectorAlocat,
    InDesfasurare,
    Conforma,
    Neconforma,
    InReinspectie,
    Inchisa,
    Anulata
}
