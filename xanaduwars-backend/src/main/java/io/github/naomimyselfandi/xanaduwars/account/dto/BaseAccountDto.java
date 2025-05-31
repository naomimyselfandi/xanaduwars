package io.github.naomimyselfandi.xanaduwars.account.dto;

import io.github.naomimyselfandi.xanaduwars.account.value.Username;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

/// The basic DTO representing an account.
@Data
public class BaseAccountDto implements AccountDto {
    private UUID id;
    private Username username;
    private Instant createdAt;
    private Instant lastSeenAt;
    private boolean admin;
    private boolean moderator;
    private boolean support;
    private boolean judge;
    private boolean developer;
}
