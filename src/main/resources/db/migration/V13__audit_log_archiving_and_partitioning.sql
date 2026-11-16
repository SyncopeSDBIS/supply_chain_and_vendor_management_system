-- V13__audit_log_archiving_and_partitioning.sql
-- Cold-storage archive table for historical audit logs older than retention period (OPS-01)
-- 100% portable DDL across PostgreSQL 16 and H2.

CREATE TABLE shared_audit_log_archive (
    id UUID PRIMARY KEY,
    timestamp TIMESTAMP NOT NULL,
    actiune VARCHAR(255) NOT NULL,
    obiect TEXT NOT NULL
);

CREATE INDEX idx_audit_log_archive_timestamp ON shared_audit_log_archive(timestamp);
CREATE INDEX idx_audit_log_archive_actiune ON shared_audit_log_archive(actiune);
