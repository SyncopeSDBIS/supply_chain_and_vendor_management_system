package ro.uaic.feaa.sdbis.supplychain.calitate.domain;

import ro.uaic.feaa.sdbis.supplychain.shared.annotation.ValueObject;

@ValueObject
public enum StareFurnizor {
    Candidat,
    InEvaluare,
    Aprobat,
    Conditionat,
    Suspendat,
    Blocat
}
