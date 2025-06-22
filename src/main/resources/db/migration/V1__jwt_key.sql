CREATE TABLE jwt_key (
    "id" UUID PRIMARY KEY,
    "encoded_secret" TEXT NOT NULL,
    "purpose" TEXT NOT NULL,
    "expiry" TIMESTAMPTZ NOT NULL
);
