-- V14: RabbitMQ AMQP DLQ Parking Lot for poison pill persistence and re-drive capability (AMQP-AUDIT-06)
CREATE TABLE IF NOT EXISTS shared_dlq_parking_lot (
    id UUID NOT NULL PRIMARY KEY,
    consumer_id VARCHAR(100),
    routing_key VARCHAR(255),
    payload TEXT,
    error_message TEXT,
    failed_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    retried BOOLEAN NOT NULL DEFAULT FALSE,
    retried_at TIMESTAMP WITH TIME ZONE
);

CREATE INDEX IF NOT EXISTS idx_dlq_parking_failed_at ON shared_dlq_parking_lot (failed_at);
CREATE INDEX IF NOT EXISTS idx_dlq_parking_retried ON shared_dlq_parking_lot (retried);
