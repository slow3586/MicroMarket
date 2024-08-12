-- liquibase formatted sql
CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- changeset slow3586:1
CREATE TABLE user_
(
    id       UUID NOT NULL,
    login    VARCHAR(16),
    password VARCHAR(255),
    name     VARCHAR(16),
    CONSTRAINT pk_user_ PRIMARY KEY (id)
);

ALTER TABLE user_
    ADD CONSTRAINT uc_user__login UNIQUE (login);

ALTER TABLE user_
    ADD CONSTRAINT uc_user__name UNIQUE (name);

CREATE INDEX idx_a62473490b3e4578fd683235c ON user_ (login);