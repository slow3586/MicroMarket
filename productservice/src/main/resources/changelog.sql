-- liquibase formatted sql
CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- changeset slow3586:1
CREATE TABLE product
(
    id         UUID    NOT NULL,
    seller_id  UUID    NOT NULL,
    name       VARCHAR(64),
    price      INTEGER NOT NULL,
    created_at TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT pk_product PRIMARY KEY (id)
);