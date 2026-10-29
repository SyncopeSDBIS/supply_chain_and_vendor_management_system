-- ===================================================================
-- V5: Performance Indexes & Database Integrity CHECK Constraints
-- ===================================================================

-- 1. Performance Indexes for Foreign Keys & Outbox / Inbox Polling
CREATE INDEX idx_comenzi_linii_comanda ON comenzi_linii(comanda_id);
CREATE INDEX idx_comenzi_loturi_comanda ON comenzi_loturi(comanda_id);
CREATE INDEX idx_comenzi_loturi_cod ON comenzi_loturi(cod_lot);
CREATE INDEX idx_comenzi_expedieri_comanda ON comenzi_expedieri(comanda_id);
CREATE INDEX idx_comenzi_furnizor_id ON comenzi_aprovizionare(furnizor_id);
CREATE INDEX idx_comenzi_stare ON comenzi_aprovizionare(stare_comanda);
CREATE INDEX idx_comenzi_data ON comenzi_aprovizionare(data_comanda);

CREATE INDEX idx_calitate_scorecards_furnizor ON calitate_scorecards(furnizor_id);
CREATE INDEX idx_calitate_inspectori_cert_insp ON calitate_inspectori_certificari(inspector_id);
CREATE INDEX idx_calitate_inspectori_disp_insp ON calitate_inspectori_disponibilitate(inspector_id);
CREATE INDEX idx_calitate_inspectii_lot ON calitate_inspectii(lot_id);
CREATE INDEX idx_calitate_inspectii_furnizor ON calitate_inspectii(furnizor_id);
CREATE INDEX idx_calitate_inspectii_comanda ON calitate_inspectii(comanda_id);
CREATE INDEX idx_calitate_inspectii_stare ON calitate_inspectii(stare_inspectie);
CREATE INDEX idx_calitate_ncr_inspectie ON calitate_ncr(inspectie_id);
CREATE INDEX idx_calitate_ncr_furnizor ON calitate_ncr(furnizor_id);
CREATE INDEX idx_calitate_ncr_stare ON calitate_ncr(stare_ncr);

CREATE INDEX idx_facturi_linii_factura ON facturi_linii(factura_id);
CREATE INDEX idx_facturi_furnizor ON facturi_furnizori(furnizor_id);
CREATE INDEX idx_facturi_comanda ON facturi_furnizori(comanda_id);
CREATE INDEX idx_facturi_stare ON facturi_furnizori(stare_factura);
CREATE INDEX idx_facturi_scadenta ON facturi_furnizori(data_scadenta);
CREATE INDEX idx_facturi_nir_comanda ON facturi_nir(comanda_id);
CREATE INDEX idx_facturi_nir_linii_nir ON facturi_nir_linii(nir_id);
CREATE INDEX idx_facturi_ordine_plata_factura ON facturi_ordine_plata(factura_id);
CREATE INDEX idx_facturi_ordine_plata_stare ON facturi_ordine_plata(stare_ordin);

CREATE INDEX idx_outbox_polling ON shared_outbox(published, timestamp);
CREATE INDEX idx_inbox_polling ON shared_inbox(event_id, consumer);
CREATE INDEX idx_audit_log_timestamp ON shared_audit_log(timestamp);

-- 2. Domain Integrity CHECK Constraints
ALTER TABLE comenzi_linii ADD CONSTRAINT chk_comenzi_linii_cantitate CHECK (cantitate > 0);
ALTER TABLE comenzi_linii ADD CONSTRAINT chk_comenzi_linii_pret CHECK (pret_unitar >= 0);
ALTER TABLE comenzi_loturi ADD CONSTRAINT chk_comenzi_loturi_cantitate CHECK (cantitate > 0);
ALTER TABLE comenzi_loturi ADD CONSTRAINT chk_comenzi_loturi_cantitate_primita CHECK (cantitate_primita >= 0);
ALTER TABLE comenzi_loturi ADD CONSTRAINT chk_comenzi_loturi_date CHECK (data_expirare >= data_productie);
ALTER TABLE comenzi_aprovizionare ADD CONSTRAINT chk_comenzi_date_livrare CHECK (data_livrare_ceruta >= data_comanda);

ALTER TABLE calitate_scorecards ADD CONSTRAINT chk_scorecards_otif CHECK (otif >= 0.00 AND otif <= 100.00);
ALTER TABLE calitate_scorecards ADD CONSTRAINT chk_scorecards_respingere CHECK (rata_respingere >= 0.00 AND rata_respingere <= 100.00);
ALTER TABLE calitate_scorecards ADD CONSTRAINT chk_scorecards_pret CHECK (conformitate_pret >= 0.00 AND conformitate_pret <= 100.00);
ALTER TABLE calitate_scorecards ADD CONSTRAINT chk_scorecards_scor CHECK (scor >= 0.00 AND scor <= 100.00);
ALTER TABLE calitate_scorecards ADD CONSTRAINT chk_scorecards_date CHECK (data_finalizare >= data_inceput);

ALTER TABLE calitate_inspectii ADD CONSTRAINT chk_inspectii_aql CHECK (numar_respingere = numar_acceptare + 1);
ALTER TABLE calitate_inspectii ADD CONSTRAINT chk_inspectii_esantion CHECK (dimensiune_esantion > 0);
ALTER TABLE calitate_inspectii ADD CONSTRAINT chk_inspectii_date CHECK (data_finalizare >= data_inceput);

ALTER TABLE facturi_linii ADD CONSTRAINT chk_facturi_linii_cantitate CHECK (cantitate > 0);
ALTER TABLE facturi_linii ADD CONSTRAINT chk_facturi_linii_pret CHECK (pret_unitar >= 0);
ALTER TABLE facturi_furnizori ADD CONSTRAINT chk_facturi_valoare_totala CHECK (valoare_totala >= 0);
ALTER TABLE facturi_furnizori ADD CONSTRAINT chk_facturi_suma_platita CHECK (suma_platita >= 0);
ALTER TABLE facturi_furnizori ADD CONSTRAINT chk_facturi_date CHECK (data_scadenta >= data_emitere);
ALTER TABLE facturi_ordine_plata ADD CONSTRAINT chk_ordine_plata_suma CHECK (suma > 0);
ALTER TABLE facturi_nir_linii ADD CONSTRAINT chk_nir_cantitate CHECK (cantitate_receptionata >= 0);
