-- liquibase formatted sql
CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- changeset slow3586:1
CREATE TABLE delivery
(
    id          UUID NOT NULL,
    order_id    UUID,
    status      VARCHAR(255),
    created_at  TIMESTAMP WITHOUT TIME ZONE,
    sent_at     TIMESTAMP WITHOUT TIME ZONE,
    received_at TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT pk_delivery PRIMARY KEY (id)
);