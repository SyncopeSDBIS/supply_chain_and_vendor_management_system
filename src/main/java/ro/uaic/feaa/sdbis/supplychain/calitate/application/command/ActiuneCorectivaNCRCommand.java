package ro.uaic.feaa.sdbis.supplychain.calitate.application.command;

import java.time.LocalDate;
import java.util.UUID;

public record ActiuneCorectivaNCRCommand(UUID ncrId, String descriere, LocalDate termenLimita) {}
