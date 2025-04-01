--liquibase formatted sql

--changeset dmitriykosolobov:1
CREATE TABLE raffles (
    id BIGSERIAL PRIMARY KEY,
    channel_id BIGINT NOT NULL UNIQUE,
    admin_id BIGINT NOT NULL UNIQUE
);

CREATE TABLE participants (
    id BIGSERIAL PRIMARY KEY,
    raffle_id BIGINT NOT NULL REFERENCES raffles(id) ON DELETE CASCADE,
    user_id BIGINT NULL,
    username TEXT NOT NULL,
    priority INT DEFAULT 0,
    UNIQUE (raffle_id, username)
);
