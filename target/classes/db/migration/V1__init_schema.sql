-- ============================================================
-- V1__init_schema.sql
-- ============================================================

CREATE TABLE IF NOT EXISTS guests (
    id          VARCHAR(36)   NOT NULL,
    full_name   VARCHAR(120)  NOT NULL,
    document    VARCHAR(30)   NOT NULL,
    email       VARCHAR(120)  NOT NULL,
    phone       VARCHAR(30),
    created_at  TIMESTAMP     DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT pk_guests       PRIMARY KEY (id),
    CONSTRAINT uq_guests_doc   UNIQUE (document),
    CONSTRAINT uq_guests_email UNIQUE (email)
);

CREATE TABLE IF NOT EXISTS rooms (
    id               VARCHAR(36)   NOT NULL,
    number           INT           NOT NULL,
    type             VARCHAR(20)   NOT NULL,
    capacity         INT           NOT NULL,
    price_per_night  DECIMAL(10,2) NOT NULL,
    status           VARCHAR(20)   NOT NULL DEFAULT 'ATIVO',
    CONSTRAINT pk_rooms        PRIMARY KEY (id),
    CONSTRAINT uq_rooms_number UNIQUE (number)
);

CREATE TABLE IF NOT EXISTS reservations (
    id                VARCHAR(36)   NOT NULL,
    guest_id          VARCHAR(36)   NOT NULL,
    room_id           VARCHAR(36)   NOT NULL,
    checkin_expected  DATE          NOT NULL,
    checkout_expected DATE          NOT NULL,
    checkin_at        TIMESTAMP,
    checkout_at       TIMESTAMP,
    status            VARCHAR(20)   NOT NULL DEFAULT 'CREATED',
    num_guests        INT           NOT NULL DEFAULT 1,
    estimated_amount  DECIMAL(10,2),
    final_amount      DECIMAL(10,2),
    created_at        TIMESTAMP     DEFAULT CURRENT_TIMESTAMP,
    updated_at        TIMESTAMP     DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT pk_reservations        PRIMARY KEY (id),
    CONSTRAINT fk_reservations_guest  FOREIGN KEY (guest_id) REFERENCES guests(id),
    CONSTRAINT fk_reservations_room   FOREIGN KEY (room_id)  REFERENCES rooms(id)
);

CREATE INDEX IF NOT EXISTS idx_rooms_status            ON rooms (status);
CREATE INDEX IF NOT EXISTS idx_reservations_room       ON reservations (room_id);
CREATE INDEX IF NOT EXISTS idx_reservations_status     ON reservations (status);
CREATE INDEX IF NOT EXISTS idx_reservations_dates      ON reservations (checkin_expected, checkout_expected);
