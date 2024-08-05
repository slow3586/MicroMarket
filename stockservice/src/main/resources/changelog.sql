-- liquibase formatted sql
CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- changeset slow3586:1
CREATE TABLE stock_change
(
    id         UUID    NOT NULL,
    product_id UUID    NOT NULL,
    order_id   UUID    NOT NULL,
    value      INTEGER NOT NULL,
    status     VARCHAR(255),
    CONSTRAINT pk_stock_change PRIMARY KEY (id)
);

ALTER TABLE stock_change
    ADD CONSTRAINT uc_stock_change_orderid UNIQUE (order_id);