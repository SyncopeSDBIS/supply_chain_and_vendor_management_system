-- ===================================================================
-- V12: Unique Supplier Invoice Constraint, NIR Storno Audit, Pre-seeded Distributed Locks
-- ===================================================================

-- 1. FIX JPA-03: Constrangere unica pe facturile furnizor (furnizor_id, serie, numar)
CREATE UNIQUE INDEX IF NOT EXISTS idx_facturi_furnizor_serie_numar 
ON facturi_furnizori(furnizor_id, serie, numar);

-- 2. FIX DDD-02: Coloana motiv_stornare pe NIR
ALTER TABLE facturi_nir ADD COLUMN IF NOT EXISTS motiv_stornare VARCHAR(500);

-- 3. FIX CONC-01 & JPA-F01: Pre-seeding determinist pentru Distributed Locks (compatibil universal PostgreSQL 16 si H2)
INSERT INTO distributed_locks (lock_name, locked_until, locked_by)
SELECT 'outboxDispatcher', TIMESTAMP '1970-01-01 00:00:00', 'init'
WHERE NOT EXISTS (SELECT 1 FROM distributed_locks WHERE lock_name = 'outboxDispatcher');

INSERT INTO distributed_locks (lock_name, locked_until, locked_by)
SELECT 'verificaTimeoutConfirmareComenzi', TIMESTAMP '1970-01-01 00:00:00', 'init'
WHERE NOT EXISTS (SELECT 1 FROM distributed_locks WHERE lock_name = 'verificaTimeoutConfirmareComenzi');

INSERT INTO distributed_locks (lock_name, locked_until, locked_by)
SELECT 'calculScorecardLunarAutomat', TIMESTAMP '1970-01-01 00:00:00', 'init'
WHERE NOT EXISTS (SELECT 1 FROM distributed_locks WHERE lock_name = 'calculScorecardLunarAutomat');

INSERT INTO distributed_locks (lock_name, locked_until, locked_by)
SELECT 'verificaExpirareCertificari', TIMESTAMP '1970-01-01 00:00:00', 'init'
WHERE NOT EXISTS (SELECT 1 FROM distributed_locks WHERE lock_name = 'verificaExpirareCertificari');

INSERT INTO distributed_locks (lock_name, locked_until, locked_by)
SELECT 'verificaScadenteFacturi', TIMESTAMP '1970-01-01 00:00:00', 'init'
WHERE NOT EXISTS (SELECT 1 FROM distributed_locks WHERE lock_name = 'verificaScadenteFacturi');

INSERT INTO distributed_locks (lock_name, locked_until, locked_by)
SELECT 'purjareEvenimenteProcesateOutboxSiInbox', TIMESTAMP '1970-01-01 00:00:00', 'init'
WHERE NOT EXISTS (SELECT 1 FROM distributed_locks WHERE lock_name = 'purjareEvenimenteProcesateOutboxSiInbox');
