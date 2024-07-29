-- liquibase formatted sql
CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- changeset slow3586:1
CREATE TABLE "user"
(
    id       UUID         NOT NULL,
    login    TEXT NOT NULL,
    password TEXT NOT NULL,
    CONSTRAINT pk_user PRIMARY KEY (id)
);