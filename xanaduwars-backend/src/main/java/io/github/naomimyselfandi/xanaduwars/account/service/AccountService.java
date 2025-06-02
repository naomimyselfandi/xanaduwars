package io.github.naomimyselfandi.xanaduwars.account.service;

import io.github.naomimyselfandi.xanaduwars.account.dto.AccountDto;
import io.github.naomimyselfandi.xanaduwars.account.entity.Account;
import io.github.naomimyselfandi.xanaduwars.account.value.*;

import java.util.Optional;

/// A service for working with accounts.
public interface AccountService {

    /// Find an account by its ID.
    <T extends AccountDto> Optional<T> find(Class<T> dto, AccountId id);

    /// Find an account by its username.
    <T extends AccountDto> Optional<T> find(Class<T> dto, Username username);

    /// Find an account by its email address.
    <T extends AccountDto> Optional<T> find(Class<T> dto, EmailAddress emailAddress);

    /// Create an account, unless the given username or email address is in use.
    /// The return value indicates whether an account was created.
    Optional<Account> create(Username username, EmailAddress emailAddress, Hash secret);

}
