package io.github.naomimyselfandi.xanaduwars.security.dto;

import io.github.naomimyselfandi.xanaduwars.account.entity.Account;
import io.github.naomimyselfandi.xanaduwars.security.entity.AuditLog;
import io.github.naomimyselfandi.xanaduwars.util.Id;
import org.jetbrains.annotations.Nullable;

import java.time.Instant;

/// A DTO representing an audit log entry.
public record AuditLogDto(
    Id<AuditLog> id,
    Instant timestamp,
    @Nullable Id<Account> accountId,
    @Nullable String username,
    @Nullable String httpMethod,
    @Nullable String httpPath,
    @Nullable String httpQuery,
    @Nullable String httpBody,
    String action,
    String sourceClass,
    String sourceMethod
) {}
