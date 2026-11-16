package ro.uaic.feaa.sdbis.supplychain.comenzi.application.command;

import ro.uaic.feaa.sdbis.supplychain.shared.IdComanda;

public record AnuleazaComandaCommand(IdComanda comandaId, String motiv) {

    public AnuleazaComandaCommand(IdComanda comandaId) {
        this(comandaId, null);
    }
}
