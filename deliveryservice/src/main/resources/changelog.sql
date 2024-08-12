-- liquibase formatted sql
CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- changeset slow3586:1
CREATE TABLE delivery
(
    id          UUID         NOT NULL,
    order_id    UUID         NOT NULL,
    status      VARCHAR(255) NOT NULL,
    created_at  TIMESTAMP WITHOUT TIME ZONE,
    sent_at     TIMESTAMP WITHOUT TIME ZONE,
    received_at TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT pk_delivery PRIMARY KEY (id)
);

ALTER TABLE delivery
    ADD CONSTRAINT uc_delivery_orderid UNIQUE (order_id);

CREATE INDEX idx_1ec4e2fcb7361059b856d1220 ON delivery (order_id);

CREATE INDEX idx_2d08c03b24685eef58a4c12fe ON delivery (created_at);

CREATE INDEX idx_37e0fb60accef464ad365ae11 ON delivery (received_at);

CREATE INDEX idx_5fcbd200dc86e3af7cec534f8 ON delivery (sent_at);

CREATE INDEX idx_d89f57080f08c18ebcf51d333 ON delivery (status);