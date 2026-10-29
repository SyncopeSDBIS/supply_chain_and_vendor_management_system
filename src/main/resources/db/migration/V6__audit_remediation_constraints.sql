-- ===================================================================
-- V6: Database Constraint Parity & Fiscal/IBAN Verification
-- Enterprise Architecture Review Board Remediation
-- ===================================================================

-- 1. Fiscal Code and IBAN Format Checks
ALTER TABLE calitate_furnizori 
    ADD CONSTRAINT chk_furnizor_cod_fiscal_len CHECK (length(cod_fiscal) >= 2);

ALTER TABLE calitate_furnizori 
    ADD CONSTRAINT chk_furnizor_iban_fmt CHECK (iban_valoare IS NULL OR length(iban_valoare) >= 10);

-- 2. Currency Code ISO-4217 Length Checks (3-character codes: EUR, RON, USD)
ALTER TABLE comenzi_linii 
    ADD CONSTRAINT chk_comenzi_linii_moneda_len CHECK (length(moneda) = 3);

ALTER TABLE facturi_furnizori 
    ADD CONSTRAINT chk_facturi_moneda_len CHECK (length(moneda) = 3);

ALTER TABLE facturi_ordine_plata 
    ADD CONSTRAINT chk_ordine_plata_moneda_len CHECK (length(moneda) = 3);

-- 3. Invoice Positive Amounts
ALTER TABLE facturi_furnizori
    ADD CONSTRAINT chk_facturi_valoare_fara_tva CHECK (valoare_fara_tva >= 0);

ALTER TABLE facturi_furnizori
    ADD CONSTRAINT chk_facturi_valoare_tva CHECK (valoare_tva >= 0);
