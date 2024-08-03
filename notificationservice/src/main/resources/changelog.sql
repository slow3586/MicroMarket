-- liquibase formatted sql
CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- changeset slow3586:1
CREATE TABLE notification
(
    id         UUID                        NOT NULL,
    user_id    UUID                        NOT NULL,
    text       VARCHAR(255),
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    CONSTRAINT pk_notification PRIMARY KEY (id)
);