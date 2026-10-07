CREATE TABLE IF NOT EXISTS test_metadata.received_notifications (
    notification_id uuid PRIMARY KEY,
    recipient text NOT NULL,
    message text NOT NULL,
    received_at timestamptz NOT NULL DEFAULT CURRENT_TIMESTAMP
);