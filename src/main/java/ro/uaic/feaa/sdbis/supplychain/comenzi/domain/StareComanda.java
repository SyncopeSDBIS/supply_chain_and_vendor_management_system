package ro.uaic.feaa.sdbis.supplychain.comenzi.domain;

import ro.uaic.feaa.sdbis.supplychain.shared.annotation.ValueObject;

@ValueObject
public enum StareComanda {
    Solicitat,
    Aprobat,
    Confirmat,
    Refuzat,
    InTranzit,
    ReceptionatPartial,
    Receptionat,
    Inchis,
    Anulat;

    // Diacritic aliases maintained for backwards-compatibility with external integration
    @Deprecated(since = "1.0.0")
    public static final StareComanda Solicitată = Solicitat;
    @Deprecated(since = "1.0.0")
    public static final StareComanda Confirmată = Confirmat;
    @Deprecated(since = "1.0.0")
    public static final StareComanda Refuzată = Refuzat;
    @Deprecated(since = "1.0.0")
    public static final StareComanda ReceptionatPartială = ReceptionatPartial;
    @Deprecated(since = "1.0.0")
    public static final StareComanda Receptionată = Receptionat;
    @Deprecated(since = "1.0.0")
    public static final StareComanda Anulată = Anulat;

    public String getDenumireAfisare() {
        return switch (this) {
            case Solicitat -> "Solicitată";
            case Aprobat -> "Aprobată";
            case Confirmat -> "Confirmată";
            case Refuzat -> "Refuzată";
            case InTranzit -> "În Tranzit";
            case ReceptionatPartial -> "Recepționată Parțial";
            case Receptionat -> "Recepționată";
            case Inchis -> "Închisă";
            case Anulat -> "Anulată";
        };
    }
}
