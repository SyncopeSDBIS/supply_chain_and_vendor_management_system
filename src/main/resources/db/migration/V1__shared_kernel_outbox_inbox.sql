CREATE TABLE shared_outbox (
    id UUID PRIMARY KEY,
    timestamp TIMESTAMP NOT NULL,
    event_type VARCHAR(255) NOT NULL,
    payload TEXT NOT NULL,
    published BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE TABLE shared_inbox (
    id VARCHAR(128) PRIMARY KEY,
    event_id UUID NOT NULL,
    consumer VARCHAR(255) NOT NULL,
    processed_at TIMESTAMP NOT NULL
);

CREATE TABLE shared_audit_log (
    id UUID PRIMARY KEY,
    timestamp TIMESTAMP NOT NULL,
    actiune VARCHAR(255) NOT NULL,
    obiect TEXT NOT NULL
);
