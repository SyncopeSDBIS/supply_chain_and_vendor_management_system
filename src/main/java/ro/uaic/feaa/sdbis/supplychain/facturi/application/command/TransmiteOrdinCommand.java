package ro.uaic.feaa.sdbis.supplychain.facturi.application.command;

import java.util.UUID;

public record TransmiteOrdinCommand(UUID ordinId, String ibanFurnizor) {}
