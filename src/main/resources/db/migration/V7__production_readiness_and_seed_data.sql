-- ===================================================================
-- V7: Production Readiness: Composite Uniqueness & Enterprise Seed Data
-- ===================================================================

-- 1. Uniqueness constraint to prevent duplicate monthly scorecards
ALTER TABLE calitate_scorecards
    ADD CONSTRAINT uq_scorecards_furnizor_perioada UNIQUE (furnizor_id, data_inceput, data_finalizare);

-- 2. Master Seed Data: Suppliers
INSERT INTO calitate_furnizori (
    id_val, cod_fiscal, denumire, strada, numar, localitate, cod_postal,
    iban_valoare, termen_plata_zile, moneda, stare_furnizor, inspectii_neconforme_consecutive, version
) VALUES (
    '11111111-1111-1111-1111-111111111111',
    'RO12345678',
    'SC TECH COMPONENTS SRL',
    'Strada Industriilor',
    '10',
    'Iasi',
    '700001',
    'RO49AAAA1B31007593840000',
    30,
    'EUR',
    'Calificat',
    0,
    0
) ON CONFLICT DO NOTHING;

INSERT INTO calitate_furnizori_emailuri (furnizor_id, email)
VALUES ('11111111-1111-1111-1111-111111111111', 'contact@techcomponents.ro')
ON CONFLICT DO NOTHING;

INSERT INTO calitate_furnizori_categorii (furnizor_id, categorie)
VALUES ('11111111-1111-1111-1111-111111111111', 'ELECTRONICE')
ON CONFLICT DO NOTHING;

INSERT INTO furnizor_preturi_negociate (furnizor_id_val, cod_material, pret)
VALUES ('11111111-1111-1111-1111-111111111111', 'MAT-SENZOR-01', 25.5000)
ON CONFLICT DO NOTHING;

INSERT INTO calitate_furnizori (
    id_val, cod_fiscal, denumire, strada, numar, localitate, cod_postal,
    iban_valoare, termen_plata_zile, moneda, stare_furnizor, inspectii_neconforme_consecutive, version
) VALUES (
    '22222222-2222-2222-2222-222222222222',
    'RO87654321',
    'SC RAW MATERIALS IMPEX SA',
    'Bulevardul Metalurgiei',
    '45',
    'Bucuresti',
    '010001',
    'RO29RZBR0000060004443333',
    45,
    'RON',
    'Candidat',
    0,
    0
) ON CONFLICT DO NOTHING;

INSERT INTO calitate_furnizori_emailuri (furnizor_id, email)
VALUES ('22222222-2222-2222-2222-222222222222', 'office@rawmaterials.ro')
ON CONFLICT DO NOTHING;

INSERT INTO calitate_furnizori_categorii (furnizor_id, categorie)
VALUES ('22222222-2222-2222-2222-222222222222', 'MATERII_PRIME')
ON CONFLICT DO NOTHING;

-- 3. Master Seed Data: Quality Inspectors
INSERT INTO calitate_inspectori (id, first_name, last_name, version)
VALUES ('33333333-3333-3333-3333-333333333333', 'Ionel', 'Popescu', 0)
ON CONFLICT DO NOTHING;

INSERT INTO calitate_inspectori_certificari (inspector_id, denumire, data_expirare)
VALUES ('33333333-3333-3333-3333-333333333333', 'ISO 9001:2015 Lead Auditor', '2028-12-31')
ON CONFLICT DO NOTHING;

INSERT INTO calitate_inspectori_disponibilitate (inspector_id, data_inceput, data_finalizare)
VALUES ('33333333-3333-3333-3333-333333333333', '2026-01-01', '2027-12-31')
ON CONFLICT DO NOTHING;

INSERT INTO calitate_inspectori (id, first_name, last_name, version)
VALUES ('44444444-4444-4444-4444-444444444444', 'Maria', 'Ionescu', 0)
ON CONFLICT DO NOTHING;

INSERT INTO calitate_inspectori_certificari (inspector_id, denumire, data_expirare)
VALUES ('44444444-4444-4444-4444-444444444444', 'ISO 14001 Environmental Auditor', '2028-06-30')
ON CONFLICT DO NOTHING;

INSERT INTO calitate_inspectori_disponibilitate (inspector_id, data_inceput, data_finalizare)
VALUES ('44444444-4444-4444-4444-444444444444', '2026-01-01', '2027-12-31')
ON CONFLICT DO NOTHING;

