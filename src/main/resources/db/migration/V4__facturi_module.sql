CREATE TABLE facturi_furnizori (
    id_factura UUID PRIMARY KEY,
    serie VARCHAR(50) NOT NULL,
    numar VARCHAR(50) NOT NULL,
    data_emitere DATE NOT NULL,
    data_scadenta DATE NOT NULL,
    comanda_id UUID NOT NULL,
    furnizor_id UUID NOT NULL,
    valoare_fara_tva NUMERIC(18, 4) NOT NULL,
    valoare_tva NUMERIC(18, 4) NOT NULL,
    valoare_totala NUMERIC(18, 4) NOT NULL,
    suma_platita NUMERIC(18, 4) NOT NULL DEFAULT 0,
    moneda VARCHAR(3) NOT NULL,
    stare_factura VARCHAR(50) NOT NULL,
    version BIGINT NOT NULL DEFAULT 0
);

CREATE TABLE facturi_linii (
    factura_id UUID NOT NULL REFERENCES facturi_furnizori(id_factura) ON DELETE CASCADE,
    cod_material VARCHAR(50) NOT NULL,
    cantitate NUMERIC(18, 4) NOT NULL,
    unitate_masura VARCHAR(20) NOT NULL,
    pret_unitar NUMERIC(18, 4) NOT NULL,
    moneda VARCHAR(3) NOT NULL,
    cota_tva NUMERIC(5, 2) NOT NULL
);

CREATE TABLE facturi_nir (
    id_nir UUID PRIMARY KEY,
    numar_nir VARCHAR(100) NOT NULL UNIQUE,
    comanda_id UUID NOT NULL,
    event_sursa_id UUID NOT NULL UNIQUE,
    data_receptie DATE NOT NULL,
    stare_nir VARCHAR(50) NOT NULL,
    version BIGINT NOT NULL DEFAULT 0
);

CREATE TABLE facturi_nir_linii (
    nir_id UUID NOT NULL REFERENCES facturi_nir(id_nir) ON DELETE CASCADE,
    lot_id UUID NOT NULL,
    cod_material VARCHAR(50) NOT NULL,
    cantitate_receptionata NUMERIC(18, 4) NOT NULL,
    unitate_masura VARCHAR(20) NOT NULL
);

CREATE TABLE facturi_ordine_plata (
    id_ordin UUID PRIMARY KEY,
    factura_id UUID NOT NULL REFERENCES facturi_furnizori(id_factura),
    suma NUMERIC(18, 4) NOT NULL,
    moneda VARCHAR(3) NOT NULL,
    creat_de UUID NOT NULL,
    aprobat_de UUID,
    stare_ordin VARCHAR(50) NOT NULL,
    version BIGINT NOT NULL DEFAULT 0
);

CREATE TABLE facturi_furnizori_blocati (
    furnizor_id UUID PRIMARY KEY,
    data_blocare TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
