package io.github.naomimyselfandi.xanaduwars.e2e;

import io.github.naomimyselfandi.xanaduwars.account.entity.Account;
import io.github.naomimyselfandi.xanaduwars.account.value.EmailAddress;
import io.github.naomimyselfandi.xanaduwars.account.value.Plaintext;
import io.github.naomimyselfandi.xanaduwars.account.value.Username;
import io.github.naomimyselfandi.xanaduwars.util.Id;
import lombok.Builder;

/// A test user. End-to-end tests can execute requests as a test user.
@Builder(toBuilder = true)
public record TestUser(Id<Account> id, Username username, Plaintext password, EmailAddress emailAddress) {}
