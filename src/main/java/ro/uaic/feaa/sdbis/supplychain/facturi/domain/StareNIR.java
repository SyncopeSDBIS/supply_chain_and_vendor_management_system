package ro.uaic.feaa.sdbis.supplychain.facturi.domain;

import ro.uaic.feaa.sdbis.supplychain.shared.annotation.ValueObject;

@ValueObject
public enum StareNIR {
    Generat,
    InReconciliere,
    CuDiscrepante,
    Reconciliat,
    Contabilizat,
    Stornat
}
