-- ===================================================================
-- V8: Enterprise Readiness: Securitate Utilizatori, Distributed Locks, Documente Atasate
-- ===================================================================

-- 1. Tabela Utilizatori pentru Autentificare Reala (JWT / BCrypt)
CREATE TABLE IF NOT EXISTS securitate_utilizatori (
    id UUID PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    full_name VARCHAR(100) NOT NULL,
    rol VARCHAR(50) NOT NULL,
    activ BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 2. Tabela Distributed Locks pentru Schedulere in Cluster / Scalare Orizontala
CREATE TABLE IF NOT EXISTS distributed_locks (
    lock_name VARCHAR(100) PRIMARY KEY,
    locked_until TIMESTAMP WITH TIME ZONE NOT NULL,
    locked_by VARCHAR(100) NOT NULL
);

-- 3. Tabela Documente Fizice Atasate (Facturi Scanate, NIR Semnat, Certificate Calitate)
CREATE TABLE IF NOT EXISTS shared_documente_atasate (
    id UUID PRIMARY KEY,
    tip_document VARCHAR(50) NOT NULL,
    referinta_id UUID NOT NULL,
    nume_fisier VARCHAR(255) NOT NULL,
    tip_continut VARCHAR(100) NOT NULL,
    dimensiune_bytes BIGINT NOT NULL,
    continut BYTEA,
    data_incarcare TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_documente_referinta ON shared_documente_atasate(referinta_id);
CREATE INDEX IF NOT EXISTS idx_documente_tip ON shared_documente_atasate(tip_document);
