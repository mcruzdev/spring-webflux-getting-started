CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE plan (
    id UUID NOT NULL DEFAULT uuid_generate_v1(),
    status VARCHAR NOT NULL,
    description VARCHAR NOT NULL,
    name VARCHAR NOT NULL,
    CONSTRAINT plan_id PRIMARY KEY(id)
);