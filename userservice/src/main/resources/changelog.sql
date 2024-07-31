-- liquibase formatted sql
CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- changeset slow3586:1
CREATE TABLE "user"
(
    id       UUID NOT NULL,
    login    VARCHAR(16),
    password VARCHAR(255),
    name     VARCHAR(16),
    CONSTRAINT pk_user PRIMARY KEY (id)
);