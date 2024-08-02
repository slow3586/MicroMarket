-- liquibase formatted sql
CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- changeset slow3586:1
CREATE TABLE product
(
    id        UUID    NOT NULL,
    seller_id UUID,
    name      VARCHAR(255),
    price     INTEGER NOT NULL,
    CONSTRAINT pk_product PRIMARY KEY (id)
);