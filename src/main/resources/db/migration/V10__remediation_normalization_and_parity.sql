-- ===================================================================
-- V10: Enterprise Persistence Remediation, Relational Normalization (1NF) & Parity
-- ===================================================================

-- 1. Normalizare tabela comenzi_expedieri si comenzi_expedieri_loturi (1NF)
ALTER TABLE comenzi_expedieri ADD COLUMN IF NOT EXISTS id UUID;

CREATE TABLE IF NOT EXISTS comenzi_expedieri_loturi (
    expediere_id UUID NOT NULL,
    lot_id UUID NOT NULL,
    PRIMARY KEY (expediere_id, lot_id)
);

CREATE INDEX IF NOT EXISTS idx_expedieri_loturi_exp ON comenzi_expedieri_loturi(expediere_id);
CREATE INDEX IF NOT EXISTS idx_expedieri_loturi_lot ON comenzi_expedieri_loturi(lot_id);

-- 2. Aliniere constrangeri integritate baza de date cu invarianti de domeniu (TVA 0.00 .. 1.00)
ALTER TABLE facturi_linii ADD CONSTRAINT chk_facturi_linii_cota_tva CHECK (cota_tva >= 0.00 AND cota_tva <= 1.00);

-- 3. Indici compusi dedicati pentru schedulere si procesari de timeout/scadenta (fara table scan)
CREATE INDEX IF NOT EXISTS idx_comenzi_stare_data ON comenzi_aprovizionare(stare_comanda, data_comanda);
CREATE INDEX IF NOT EXISTS idx_facturi_stare_scad ON facturi_furnizori(stare_factura, data_scadenta);
CREATE INDEX IF NOT EXISTS idx_inspectii_furn_date ON calitate_inspectii(furnizor_id, data_inceput, data_finalizare);

