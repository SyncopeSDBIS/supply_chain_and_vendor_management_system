-- V11: Add Approval and Closure Audit Columns to comenzi_aprovizionare
ALTER TABLE comenzi_aprovizionare ADD COLUMN IF NOT EXISTS data_aprobare TIMESTAMP WITH TIME ZONE;
ALTER TABLE comenzi_aprovizionare ADD COLUMN IF NOT EXISTS aprobat_de UUID;
ALTER TABLE comenzi_aprovizionare ADD COLUMN IF NOT EXISTS nivel_aprobare VARCHAR(50);
ALTER TABLE comenzi_aprovizionare ADD COLUMN IF NOT EXISTS data_inchidere TIMESTAMP WITH TIME ZONE;
