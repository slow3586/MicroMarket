-- liquibase formatted sql
CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- changeset slow3586:1
CREATE TABLE order_
(
    id           UUID         NOT NULL,
    product_id   UUID         NOT NULL,
    buyer_id     UUID         NOT NULL,
    quantity     INTEGER      NOT NULL,
    status       VARCHAR(255) NOT NULL,
    error        VARCHAR(255),
    created_at   TIMESTAMP WITHOUT TIME ZONE,
    activated_at TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT pk_order_ PRIMARY KEY (id)
);

CREATE INDEX idx_0fa6c32e0c12a7536d8f5fb4c ON order_ (status, created_at);

CREATE INDEX idx_20981b2b68bf03393c44dd1b9 ON order_ (buyer_id);

CREATE INDEX idx_7a9573d6a1fb982772a912332 ON order_ (status);

CREATE INDEX idx_7bb07d3c6e225d75d8418380f ON order_ (created_at);

CREATE INDEX idx_f43aa9d73dc1e87e46834a226 ON order_ (id, status);