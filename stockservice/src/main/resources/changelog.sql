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