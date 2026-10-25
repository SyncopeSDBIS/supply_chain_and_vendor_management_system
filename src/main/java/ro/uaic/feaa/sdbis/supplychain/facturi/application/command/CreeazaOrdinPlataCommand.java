package ro.uaic.feaa.sdbis.supplychain.facturi.application.command;

import ro.uaic.feaa.sdbis.supplychain.facturi.domain.Suma;
import java.util.UUID;

public record CreeazaOrdinPlataCommand(UUID facturaId, Suma suma, UUID creatDe) {}
