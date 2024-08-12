-- liquibase formatted sql
CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- changeset slow3586:1
CREATE TABLE stock_update
(
    id         UUID    NOT NULL,
    product_id UUID    NOT NULL,
    value      INTEGER NOT NULL,
    created_at TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT pk_stock_update PRIMARY KEY (id)
);

CREATE INDEX idx_9e6f96957a294961307060a71 ON stock_update (product_id);

CREATE INDEX idx_c0ecdadfa456011617353055d ON stock_update (created_at);

CREATE TABLE stock_update_order
(
    id         UUID         NOT NULL,
    product_id UUID         NOT NULL,
    order_id   UUID         NOT NULL,
    value      INTEGER      NOT NULL,
    status     VARCHAR(255) NOT NULL,
    created_at TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT pk_stock_update_order PRIMARY KEY (id)
);

ALTER TABLE stock_update_order
    ADD CONSTRAINT uc_stock_update_order_orderid UNIQUE (order_id);

CREATE INDEX idx_0f8d0809f53c02519a8237743 ON stock_update_order (status, created_at);

CREATE INDEX idx_1f654d8e78b489b101cd82621 ON stock_update_order (product_id);

CREATE INDEX idx_8702c178ed0650429550357b0 ON stock_update_order (order_id);

CREATE INDEX idx_8aa11dcdf8bdb6c24436a4b34 ON stock_update_order (status);

CREATE INDEX idx_d122f88ae9d1c151951644317 ON stock_update_order (order_id, status);