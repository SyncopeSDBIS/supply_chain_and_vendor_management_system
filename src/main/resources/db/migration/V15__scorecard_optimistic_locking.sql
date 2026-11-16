ALTER TABLE calitate_scorecards ADD COLUMN IF NOT EXISTS version BIGINT NOT NULL DEFAULT 0;

-- Index pentru optimizarea verificării version (optional, dar recomandat pentru baze mari)
-- CREATE INDEX IF NOT EXISTS idx_calitate_scorecards_version ON calitate_scorecards(ofertant_id, version);
