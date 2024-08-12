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

CREATE INDEX idx_5c381847e2b8eedef7f9210fa ON product (seller_id);

CREATE INDEX idx_b457b176870a31c4b12ea5568 ON product (created_at);