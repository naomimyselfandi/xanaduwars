CREATE EXTENSION IF NOT EXISTS citext;

CREATE TABLE account (
    "id" UUID PRIMARY KEY,
    "username" VARCHAR(64) NOT NULL,
    "canonical_username" VARCHAR(64) UNIQUE NOT NULL,
    "email_address" CITEXT UNIQUE NOT NULL,
    "password" TEXT,
    "created_at" TIMESTAMPTZ NOT NULL DEFAULT now(),
    "last_seen_at" TIMESTAMPTZ NOT NULL DEFAULT '1970-01-01T00:00:00Z',
    "remember_me" BOOLEAN NOT NULL DEFAULT FALSE,
    "roles" INTEGER NOT NULL DEFAULT 0
);
