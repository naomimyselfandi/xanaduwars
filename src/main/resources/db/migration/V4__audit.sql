CREATE TABLE audit_log (
    "id" UUID PRIMARY KEY,
    "timestamp" TIMESTAMPTZ NOT NULL DEFAULT now(),

    "account_id" UUID REFERENCES account(id) ON DELETE SET NULL,
    "username" TEXT,
    "user_email" TEXT,

    "http_method" TEXT,
    "http_path" TEXT,
    "http_query" TEXT,
    "http_body" TEXT,
    "action" TEXT NOT NULL,

    "source_class" TEXT,
    "source_method" TEXT
);

CREATE INDEX idx_audit_log_timestamp ON audit_log(timestamp);
CREATE INDEX idx_audit_log_account_id ON audit_log(account_id, timestamp);
CREATE INDEX idx_audit_log_http_path ON audit_log(http_path, timestamp);
CREATE INDEX idx_audit_log_action ON audit_log(action, timestamp);
