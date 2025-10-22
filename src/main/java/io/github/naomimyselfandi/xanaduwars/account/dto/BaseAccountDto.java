package io.github.naomimyselfandi.xanaduwars.account.dto;

import io.github.naomimyselfandi.xanaduwars.account.entity.Account;
import io.github.naomimyselfandi.xanaduwars.account.value.RoleSet;
import io.github.naomimyselfandi.xanaduwars.account.value.Username;
import io.github.naomimyselfandi.xanaduwars.util.Id;
import io.github.naomimyselfandi.xanaduwars.util.NotCovered;

import java.time.Instant;

/// A DTO representing an account's basic information.
@NotCovered
public record BaseAccountDto(
        Id<Account> id,
        Username username,
        RoleSet roles,
        Instant createdAt,
        Instant lastSeenAt
) implements AccountDto {}
