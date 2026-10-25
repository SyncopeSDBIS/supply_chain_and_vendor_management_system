CREATE TABLE shared_outbox (
    id UUID PRIMARY KEY,
    timestamp TIMESTAMP NOT NULL,
    event_type VARCHAR(255) NOT NULL,
    payload TEXT NOT NULL,
    published BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE TABLE shared_inbox (
    id UUID PRIMARY KEY,
    received_at TIMESTAMP NOT NULL,
    event_type VARCHAR(255) NOT NULL,
    processed BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE TABLE shared_audit_log (
    id UUID PRIMARY KEY,
    timestamp TIMESTAMP NOT NULL,
    actiune VARCHAR(255) NOT NULL,
    obiect TEXT NOT NULL
);
