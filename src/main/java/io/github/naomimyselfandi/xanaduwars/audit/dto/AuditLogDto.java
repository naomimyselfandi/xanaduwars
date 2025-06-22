package io.github.naomimyselfandi.xanaduwars.audit.dto;

import io.github.naomimyselfandi.xanaduwars.account.entity.Account;
import io.github.naomimyselfandi.xanaduwars.audit.entity.AuditLog;
import io.github.naomimyselfandi.xanaduwars.util.Id;
import lombok.Data;

import java.time.Instant;

/// A DTO representing an audit log entry.
@Data
public class AuditLogDto {
    private Id<AuditLog> id;
    private Instant timestamp;
    private Id<Account> accountId;
    private String username;
    private String httpMethod;
    private String httpPath;
    private String httpQuery;
    private String httpBody;
    private String action;
    private String sourceClass;
    private String sourceMethod;
}
