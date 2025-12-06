DROP TABLE IF EXISTS compilation_events CASCADE;
DROP TABLE IF EXISTS compilations CASCADE;

CREATE TABLE IF NOT EXISTS compilations (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    pinned BOOLEAN NOT NULL DEFAULT false,
    title VARCHAR(50) NOT NULL
);

CREATE TABLE IF NOT EXISTS compilation_events (
    compilation_id BIGINT NOT NULL REFERENCES compilations(id) ON DELETE CASCADE,
    event_id BIGINT NOT NULL,
    PRIMARY KEY (compilation_id, event_id)
);