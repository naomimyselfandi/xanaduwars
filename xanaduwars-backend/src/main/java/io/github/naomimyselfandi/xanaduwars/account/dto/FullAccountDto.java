package io.github.naomimyselfandi.xanaduwars.account.dto;

import io.github.naomimyselfandi.xanaduwars.account.value.EmailAddress;
import io.github.naomimyselfandi.xanaduwars.account.value.Username;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

/// A DTO representing all of an account's details, other than its password and
/// API key.
@Data
public class FullAccountDto implements AccountDto {
    private UUID id;
    private Username username;
    private EmailAddress emailAddress;
    private Instant createdAt;
    private Instant lastSeenAt;
    private boolean admin;
    private boolean moderator;
    private boolean support;
    private boolean judge;
    private boolean developer;
    private AccountSettingsDto settings;
}
