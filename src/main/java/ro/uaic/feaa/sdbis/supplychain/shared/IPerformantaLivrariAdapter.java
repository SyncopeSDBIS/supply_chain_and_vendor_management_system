package ro.uaic.feaa.sdbis.supplychain.shared;

/**
 * Port pentru obtinerea metricilor de performanta ale unui furnizor (ADR-002 Inversion of Control).
 * Implementat de modulul Comenzi si consumat de modulul Calitate fara cuplare circulara.
 */
public interface IPerformantaLivrariAdapter {
    PerformantaLivrariVO obtinePerformanta(IdFurnizor furnizorId, Perioada luna);
}

