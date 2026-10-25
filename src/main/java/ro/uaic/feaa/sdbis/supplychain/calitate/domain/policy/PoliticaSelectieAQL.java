package ro.uaic.feaa.sdbis.supplychain.calitate.domain.policy;

import ro.uaic.feaa.sdbis.supplychain.calitate.domain.Calificativ;
import ro.uaic.feaa.sdbis.supplychain.calitate.domain.PlanAQL;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Politica de selectie a regimului de inspectie si planului AQL conform ISO 2859-1 (Other Business Rules - ADR-003).
 * Adapteaza severitatea inspectiei in functie de calificativul furnizorului si marimea lotului.
 */
public class PoliticaSelectieAQL {

    public enum RegimInspectie {
        REDUS,
        NORMAL,
        SEVER,
        TOTAL_100_PROCENT
    }

    public RegimInspectie determinaRegim(Calificativ calificativ) {
        if (calificativ == null) {
            return RegimInspectie.NORMAL;
        }
        return switch (calificativ) {
            case A -> RegimInspectie.REDUS;
            case B -> RegimInspectie.NORMAL;
            case C -> RegimInspectie.SEVER;
            case D -> RegimInspectie.TOTAL_100_PROCENT;
        };
    }

    public PlanAQL determinaPlan(Calificativ calificativ, int marimeLot) {
        if (marimeLot <= 0) {
            throw new IllegalArgumentException("Marimea lotului trebuie sa fie pozitiva: " + marimeLot);
        }
        RegimInspectie regim = determinaRegim(calificativ);

        int esantion;
        int acceptare;
        double aql = 1.5; // Valoare AQL standard

        if (regim == RegimInspectie.TOTAL_100_PROCENT) {
            esantion = marimeLot;
            acceptare = 0;
            return PlanAQL.standard(0.65, esantion, acceptare);
        }

        if (marimeLot <= 50) {
            switch (regim) {
                case REDUS -> { esantion = Math.min(5, marimeLot); acceptare = 0; }
                case SEVER -> { esantion = Math.min(13, marimeLot); acceptare = 0; }
                default -> { esantion = Math.min(8, marimeLot); acceptare = 0; }
            }
        } else if (marimeLot <= 500) {
            switch (regim) {
                case REDUS -> { esantion = 20; acceptare = 1; }
                case SEVER -> { esantion = 80; acceptare = 1; }
                default -> { esantion = 50; acceptare = 2; }
            }
        } else {
            switch (regim) {
                case REDUS -> { esantion = 50; acceptare = 2; }
                case SEVER -> { esantion = 200; acceptare = 2; }
                default -> { esantion = 125; acceptare = 3; }
            }
        }

        // Asigura ca esantionul nu depaseste marimea lotului
        if (esantion > marimeLot) {
            esantion = marimeLot;
        }

        return PlanAQL.standard(aql, esantion, acceptare);
    }
}
