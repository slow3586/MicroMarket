-- liquibase formatted sql
CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- changeset slow3586:1
CREATE TABLE "order"
(
    id         UUID                        NOT NULL,
    buyer_id   UUID                        NOT NULL,
    status     VARCHAR(255),
    error      VARCHAR(255),
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    paid_at    TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT pk_order PRIMARY KEY (id)
);

CREATE TABLE order_item
(
    id         UUID    NOT NULL,
    product_id UUID    NOT NULL,
    quantity   INTEGER NOT NULL,
    status     VARCHAR(255),
    error      VARCHAR(255),
    order_id   UUID    NOT NULL,
    CONSTRAINT pk_order_item PRIMARY KEY (id)
);

ALTER TABLE order_item
    ADD CONSTRAINT FK_ORDER_ITEM_ON_ORDER FOREIGN KEY (order_id) REFERENCES "order" (id);