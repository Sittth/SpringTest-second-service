CREATE SCHEMA IF NOT EXISTS test_metadata;

CREATE TABLE IF NOT EXISTS test_metadata.book_metadata (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    book_id uuid NOT NULL UNIQUE,
    publisher text NOT NULL,
    price numeric(10, 2) NOT NULL,
    idempotency_key uuid UNIQUE
);