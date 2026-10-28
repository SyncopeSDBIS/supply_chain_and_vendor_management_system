package ro.uaic.feaa.sdbis.supplychain.calitate.application.command;

import java.util.Set;
import java.util.UUID;

public record CreeazaFurnizorCommand(
    UUID id,
    String codFiscal,
    String denumire,
    String strada,
    String oras,
    String codPostal,
    String tara,
    String iban,
    Set<String> emailuri,
    Set<String> categorii,
    int termenPlataZile
) {}
