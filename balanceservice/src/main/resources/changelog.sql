-- liquibase formatted sql
CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- changeset slow3586:1
CREATE TABLE balance_replenish
(
    id         UUID                        NOT NULL,
    user_id    UUID                        NOT NULL,
    value      INTEGER                     NOT NULL,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    CONSTRAINT pk_balance_replenish PRIMARY KEY (id)
);

CREATE TABLE balance_transfer
(
    id          UUID                        NOT NULL,
    sender_id   UUID                        NOT NULL,
    receiver_id UUID                        NOT NULL,
    order_id    UUID                        NOT NULL,
    value       INTEGER                     NOT NULL,
    status      VARCHAR(255),
    created_at  TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    CONSTRAINT pk_balance_transfer PRIMARY KEY (id)
);

ALTER TABLE balance_transfer
    ADD CONSTRAINT uc_balance_transfer_orderid UNIQUE (order_id);