package io.github.naomimyselfandi.xanaduwars.account.dto;

import io.github.naomimyselfandi.xanaduwars.account.entity.Account;
import io.github.naomimyselfandi.xanaduwars.account.value.Username;
import io.github.naomimyselfandi.xanaduwars.util.Id;
import lombok.Data;
import org.jetbrains.annotations.Nullable;

import java.time.Instant;

/// The basic DTO representing an account.
@Data
public class BaseAccountDto implements AccountDto {
    private Id<Account> id;
    private Username username;
    private Instant createdAt;
    private @Nullable Instant lastSeenAt;
    private boolean admin;
    private boolean moderator;
    private boolean support;
    private boolean judge;
    private boolean developer;
    private boolean bot;
}
