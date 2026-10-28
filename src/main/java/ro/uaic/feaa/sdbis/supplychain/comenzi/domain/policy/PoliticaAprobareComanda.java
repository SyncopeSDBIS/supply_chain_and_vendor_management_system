package ro.uaic.feaa.sdbis.supplychain.comenzi.domain.policy;

import ro.uaic.feaa.sdbis.supplychain.comenzi.domain.ComandaAprovizionare;
import ro.uaic.feaa.sdbis.supplychain.shared.annotation.DomainPolicy;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Politica de business pentru stabilirea pragurilor si nivelelor de aprobare a comenzilor de aprovizionare.
 * Conform regulilor de business (Other Business Rules - ADR-003 / Project_OOAD):
 * - Sub 10,000 RON: Responsabil Achizitii (aprobare operationala)
 * - 10,000 - 50,000 RON: Manager Departament
 * - Peste 50,000 RON: Director General / Consiliu
 */
@DomainPolicy
public class PoliticaAprobareComanda {

    public static final BigDecimal PRAG_MANAGER = BigDecimal.valueOf(10000.00);
    public static final BigDecimal PRAG_DIRECTOR = BigDecimal.valueOf(50000.00);

    public enum NivelAprobare {
        RESPONSABIL_ACHIZITII(1),
        MANAGER_DEPARTAMENT(2),
        DIRECTOR_GENERAL(3);

        private final int rang;

        NivelAprobare(int rang) {
            this.rang = rang;
        }

        public int getRang() {
            return rang;
        }
    }

    public NivelAprobare determinaNivelNecesar(BigDecimal valoareTotala) {
        Objects.requireNonNull(valoareTotala, "valoareTotala nu poate fi null");
        if (valoareTotala.compareTo(PRAG_DIRECTOR) > 0) {
            return NivelAprobare.DIRECTOR_GENERAL;
        }
        if (valoareTotala.compareTo(PRAG_MANAGER) >= 0) {
            return NivelAprobare.MANAGER_DEPARTAMENT;
        }
        return NivelAprobare.RESPONSABIL_ACHIZITII;
    }

    public NivelAprobare determinaNivelNecesar(ComandaAprovizionare comanda) {
        Objects.requireNonNull(comanda, "comanda nu poate fi null");
        return determinaNivelNecesar(comanda.calculeazaTotal());
    }

    public boolean poateAproba(NivelAprobare nivelUtilizator, BigDecimal valoareTotala) {
        Objects.requireNonNull(nivelUtilizator, "nivelUtilizator nu poate fi null");
        NivelAprobare nivelNecesar = determinaNivelNecesar(valoareTotala);
        return nivelUtilizator.getRang() >= nivelNecesar.getRang();
    }
}
