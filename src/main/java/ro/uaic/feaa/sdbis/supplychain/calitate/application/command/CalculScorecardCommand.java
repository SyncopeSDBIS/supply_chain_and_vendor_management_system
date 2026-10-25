package ro.uaic.feaa.sdbis.supplychain.calitate.application.command;

import ro.uaic.feaa.sdbis.supplychain.shared.IdFurnizor;
import ro.uaic.feaa.sdbis.supplychain.shared.Perioada;
import java.math.BigDecimal;

public record CalculScorecardCommand(IdFurnizor furnizorId, Perioada luna, BigDecimal rataRespingere) {}
