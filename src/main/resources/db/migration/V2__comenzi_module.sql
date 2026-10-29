CREATE TABLE comenzi_aprovizionare (
    id_val UUID PRIMARY KEY,
    numar_comanda VARCHAR(100) NOT NULL UNIQUE,
    furnizor_id UUID NOT NULL,
    cumparator_id UUID NOT NULL,
    data_comanda DATE NOT NULL,
    data_livrare_ceruta DATE NOT NULL,
    data_livrare_promisa DATE,
    depozit VARCHAR(255) NOT NULL,
    stare_comanda VARCHAR(50) NOT NULL,
    estimare_data DATE,
    estimare_metoda VARCHAR(100),
    version BIGINT NOT NULL DEFAULT 0
);

CREATE TABLE comenzi_linii (
    comanda_id UUID NOT NULL REFERENCES comenzi_aprovizionare(id_val) ON DELETE CASCADE,
    cod_material VARCHAR(50) NOT NULL,
    cantitate NUMERIC(18, 4) NOT NULL,
    unitate_masura VARCHAR(20) NOT NULL,
    pret_unitar NUMERIC(18, 4) NOT NULL,
    moneda VARCHAR(3) NOT NULL
);

CREATE TABLE comenzi_loturi (
    id_val UUID PRIMARY KEY,
    comanda_id UUID REFERENCES comenzi_aprovizionare(id_val) ON DELETE CASCADE,
    cod_lot VARCHAR(100) NOT NULL,
    cod_material VARCHAR(50) NOT NULL,
    cantitate NUMERIC(18, 4) NOT NULL,
    cantitate_primita NUMERIC(18, 4) NOT NULL,
    unitate_masura VARCHAR(20) NOT NULL,
    data_productie DATE NOT NULL,
    data_expirare DATE NOT NULL,
    stare_lot VARCHAR(50) NOT NULL
);

CREATE TABLE comenzi_expedieri (
    comanda_id UUID NOT NULL REFERENCES comenzi_aprovizionare(id_val) ON DELETE CASCADE,
    transportator VARCHAR(100) NOT NULL,
    nr_awb VARCHAR(100) NOT NULL,
    data_expediere DATE NOT NULL,
    loturi_ids VARCHAR(2000)
);
