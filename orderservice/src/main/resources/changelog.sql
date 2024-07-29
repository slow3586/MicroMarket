-- liquibase formatted sql
CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- changeset slow3586:1
CREATE TABLE "order"
(
    id        UUID NOT NULL,
    buyer_id  UUID,
    seller_id UUID,
    status    TEXT,
    CONSTRAINT pk_order PRIMARY KEY (id)
);

CREATE TABLE order_item
(
    id         UUID    NOT NULL,
    product_id UUID,
    quantity   INTEGER NOT NULL,
    order_id   UUID    NOT NULL,
    CONSTRAINT pk_order_item PRIMARY KEY (id)
);

ALTER TABLE order_item
    ADD CONSTRAINT FK_ORDER_ITEM_ON_ORDER FOREIGN KEY (order_id) REFERENCES "order" (id);