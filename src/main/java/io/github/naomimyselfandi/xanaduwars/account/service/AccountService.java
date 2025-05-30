package io.github.naomimyselfandi.xanaduwars.account.service;

import io.github.naomimyselfandi.xanaduwars.account.entity.Account;
import io.github.naomimyselfandi.xanaduwars.account.value.*;

import java.util.Optional;
import java.util.UUID;

/// A service for working with accounts.
public interface AccountService {

    /// Find an account by its ID.
    Optional<Account> find(UUID id);

    /// Find an account by its username.
    Optional<Account> find(Username username);

    /// Find an account by its username and authentication secret.
    Optional<Account> find(Username username, Plaintext<?> secret);

    /// Find an account by its email address.
    Optional<Account> find(EmailAddress emailAddress);

    /// Create an account and return it. If the given username or email address
    /// is already in use, this returns an empty optional.
    Optional<Account> create(Username username, EmailAddress emailAddress, Plaintext<?> secret);

}
