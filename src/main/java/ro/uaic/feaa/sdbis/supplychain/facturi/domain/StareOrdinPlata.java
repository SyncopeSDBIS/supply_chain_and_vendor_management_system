package ro.uaic.feaa.sdbis.supplychain.facturi.domain;

import ro.uaic.feaa.sdbis.supplychain.shared.annotation.ValueObject;

@ValueObject
public enum StareOrdinPlata {
    Creat,
    InAprobare,
    Aprobat,
    TransmisLaBanca,
    Executat,
    RespinsDeBanca,
    Anulat
}
