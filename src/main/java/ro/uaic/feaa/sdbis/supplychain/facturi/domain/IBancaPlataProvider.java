package ro.uaic.feaa.sdbis.supplychain.facturi.domain;

public interface IBancaPlataProvider {
    boolean transmiteOrdinPlata(OrdinPlata ordin, String ibanDestinatar);
}
