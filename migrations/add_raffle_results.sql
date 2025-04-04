--liquibase formatted sql

--changeset dmitriykosolobov:2
CREATE TABLE winners (
    id BIGSERIAL PRIMARY KEY,
    raffle_id BIGINT NOT NULL REFERENCES raffles(id) ON DELETE CASCADE,
    participant_id BIGINT NOT NULL REFERENCES participants(id) ON DELETE CASCADE,
    UNIQUE (raffle_id, participant_id)
);
