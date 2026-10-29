CREATE TABLE calitate_furnizori (
    id_val UUID PRIMARY KEY,
    cod_fiscal VARCHAR(50) NOT NULL UNIQUE,
    denumire VARCHAR(255) NOT NULL,
    strada VARCHAR(255),
    numar VARCHAR(50),
    localitate VARCHAR(100),
    cod_postal VARCHAR(20),
    iban_valoare VARCHAR(50),
    termen_plata_zile INT,
    moneda VARCHAR(3),
    stare_furnizor VARCHAR(50) NOT NULL,
    inspectii_neconforme_consecutive INT NOT NULL DEFAULT 0,
    version BIGINT NOT NULL DEFAULT 0
);

CREATE TABLE calitate_furnizori_emailuri (
    furnizor_id UUID NOT NULL REFERENCES calitate_furnizori(id_val) ON DELETE CASCADE,
    email VARCHAR(255) NOT NULL
);

CREATE TABLE calitate_furnizori_categorii (
    furnizor_id UUID NOT NULL REFERENCES calitate_furnizori(id_val) ON DELETE CASCADE,
    categorie VARCHAR(100) NOT NULL
);

CREATE TABLE furnizor_preturi_negociate (
    furnizor_id_val UUID NOT NULL REFERENCES calitate_furnizori(id_val) ON DELETE CASCADE,
    cod_material VARCHAR(50) NOT NULL,
    pret NUMERIC(18, 4) NOT NULL
);

CREATE TABLE calitate_scorecards (
    id UUID PRIMARY KEY,
    furnizor_id UUID NOT NULL REFERENCES calitate_furnizori(id_val) ON DELETE CASCADE,
    data_inceput DATE NOT NULL,
    data_finalizare DATE NOT NULL,
    otif NUMERIC(5, 2) NOT NULL,
    rata_respingere NUMERIC(5, 2) NOT NULL,
    conformitate_pret NUMERIC(5, 2) NOT NULL,
    scor NUMERIC(5, 2) NOT NULL
);

CREATE TABLE calitate_inspectori (
    id UUID PRIMARY KEY,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    version BIGINT NOT NULL DEFAULT 0
);

CREATE TABLE calitate_inspectori_certificari (
    inspector_id UUID NOT NULL REFERENCES calitate_inspectori(id) ON DELETE CASCADE,
    denumire VARCHAR(255) NOT NULL,
    data_expirare DATE NOT NULL
);

CREATE TABLE calitate_inspectori_disponibilitate (
    inspector_id UUID NOT NULL REFERENCES calitate_inspectori(id) ON DELETE CASCADE,
    data_inceput DATE NOT NULL,
    data_finalizare DATE NOT NULL
);

CREATE TABLE calitate_inspectii (
    id_inspectie UUID PRIMARY KEY,
    lot_id UUID NOT NULL,
    comanda_id UUID NOT NULL,
    furnizor_id UUID NOT NULL,
    cod_material VARCHAR(50) NOT NULL,
    data_inceput DATE NOT NULL,
    data_finalizare DATE NOT NULL,
    inspector_id UUID,
    stare_inspectie VARCHAR(50) NOT NULL,
    verdict VARCHAR(50),
    nivel_aql NUMERIC(5, 2),
    dimensiune_esantion INT,
    numar_acceptare INT,
    numar_respingere INT,
    version BIGINT NOT NULL DEFAULT 0
);

CREATE TABLE calitate_ncr (
    id_ncr UUID PRIMARY KEY,
    inspectie_id UUID NOT NULL,
    furnizor_id UUID NOT NULL,
    lot_id UUID NOT NULL,
    cauza_radacina TEXT,
    descriere_actiune TEXT,
    responsabil VARCHAR(100),
    termen DATE,
    stare_ncr VARCHAR(50) NOT NULL,
    version BIGINT NOT NULL DEFAULT 0
);
