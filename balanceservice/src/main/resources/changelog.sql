-- liquibase formatted sql
CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- changeset slow3586:1
CREATE TABLE balance_update
(
    id         UUID    NOT NULL,
    user_id    UUID    NOT NULL,
    value      INTEGER NOT NULL,
    created_at TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT pk_balance_update PRIMARY KEY (id)
);

CREATE INDEX idx_2f70f3fadbf626e0dc024f380 ON balance_update (created_at);

CREATE INDEX idx_baf858ca4e129b721ef008728 ON balance_update (user_id);

CREATE TABLE balance_update_order
(
    id          UUID         NOT NULL,
    order_id    UUID         NOT NULL,
    sender_id   UUID         NOT NULL,
    receiver_id UUID         NOT NULL,
    value       INTEGER      NOT NULL,
    status      VARCHAR(255) NOT NULL,
    created_at  TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT pk_balance_update_order PRIMARY KEY (id)
);

ALTER TABLE balance_update_order
    ADD CONSTRAINT uc_balance_update_order_orderid UNIQUE (order_id);

CREATE INDEX idx_54eba1fcb62b38c0ca81fedec ON balance_update_order (order_id);

CREATE INDEX idx_6b76ed28d7688c16f46c0ce99 ON balance_update_order (sender_id);

CREATE INDEX idx_bc8aee16e8ed8a7e3979f600a ON balance_update_order (receiver_id);

CREATE INDEX idx_d01d65fb19606b7426bb5524e ON balance_update_order (status);

CREATE INDEX idx_d20d0496b93d6c63083ff1bb6 ON balance_update_order (created_at);