CREATE TABLE email (
    "id" UUID PRIMARY KEY,
    "address" TEXT NOT NULL,
    "content" JSONB NOT NULL,
    "attempts" INTEGER NOT NULL,
    "status" TEXT NOT NULL,
    "status_time" TIMESTAMPTZ NOT NULL,
    "status_reason" TEXT NOT NULL DEFAULT ''
);

CREATE INDEX idx_email_status ON email (status, status_time);

CREATE INDEX idx_email_address ON email (address);
