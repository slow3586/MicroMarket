-- liquibase formatted sql
CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- changeset slow3586:1
CREATE TABLE balance_change
(
    id      UUID    NOT NULL,
    user_id UUID    NOT NULL,
    value   INTEGER NOT NULL,
    CONSTRAINT pk_balance_change PRIMARY KEY (id)
);