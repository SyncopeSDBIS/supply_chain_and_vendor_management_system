package ro.uaic.feaa.sdbis.supplychain.facturi.application.command;

import java.util.UUID;

/**
 * Comanda pentru inregistrarea rezultatului executiei platii de la banca (I7, I7b).
 */
public record InregistreazaExecutiePlataCommand(UUID ordinId) {}
