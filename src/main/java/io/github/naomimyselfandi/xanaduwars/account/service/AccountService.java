package io.github.naomimyselfandi.xanaduwars.account.service;

import io.github.naomimyselfandi.xanaduwars.account.dto.AccountDto;
import io.github.naomimyselfandi.xanaduwars.account.dto.AccountSettingsDto;
import io.github.naomimyselfandi.xanaduwars.account.dto.FullAccountDto;
import io.github.naomimyselfandi.xanaduwars.account.entity.Account;
import io.github.naomimyselfandi.xanaduwars.account.value.*;
import io.github.naomimyselfandi.xanaduwars.auth.dto.UserDetailsDto;
import io.github.naomimyselfandi.xanaduwars.util.Id;
import io.github.naomimyselfandi.xanaduwars.util.NoSuchEntityException;
import org.jetbrains.annotations.Nullable;

/// A service for working with accounts.
public interface AccountService {

    /// Find an account by its ID, and update its last seen timestamp.
    UserDetailsDto getForAuthentication(@Nullable Id<Account> id) throws NoSuchEntityException;

    /// Find an account by its ID.
    <T extends AccountDto> T get(Class<T> dto, @Nullable Id<Account> id) throws NoSuchEntityException;

    /// Find an account by its username.
    <T extends AccountDto> T get(Class<T> dto, Username username) throws NoSuchEntityException;

    /// Find an account by its email address.
    <T extends AccountDto> T get(Class<T> dto, EmailAddress emailAddress) throws NoSuchEntityException;

    /// Create an account, unless the given username or email address is in use.
    /// To minimize the risk of username or email enumeration, this method does
    /// not indicate whether the account was created in any way.
    void create(Username username, EmailAddress emailAddress, Password password, Role... roles);

    /// Update an account's settings.
    FullAccountDto updateSettings(Id<Account> id, AccountSettingsDto settings) throws NoSuchEntityException;

}
