-- liquibase formatted sql
CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- changeset slow3586:1
CREATE TABLE delivery
(
    id          UUID         NOT NULL,
    order_id    UUID         NOT NULL,
    status      VARCHAR(255) NOT NULL,
    created_at  TIMESTAMP WITHOUT TIME ZONE,
    sent_at     TIMESTAMP WITHOUT TIME ZONE,
    received_at TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT pk_delivery PRIMARY KEY (id)
);

ALTER TABLE delivery
    ADD CONSTRAINT uc_delivery_orderid UNIQUE (order_id);