package ro.uaic.feaa.sdbis.supplychain.comenzi.domain;

import ro.uaic.feaa.sdbis.supplychain.shared.annotation.ValueObject;

@ValueObject
public enum StareComanda {
    Solicitat,
    Confirmat,
    Refuzat,
    InTranzit,
    ReceptionatPartial,
    Receptionat,
    Anulat
}
