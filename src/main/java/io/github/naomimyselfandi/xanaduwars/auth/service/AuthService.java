package io.github.naomimyselfandi.xanaduwars.auth.service;

import io.github.naomimyselfandi.xanaduwars.account.dto.UserDetailsDto;
import io.github.naomimyselfandi.xanaduwars.account.entity.Account;
import io.github.naomimyselfandi.xanaduwars.account.value.EmailAddress;
import io.github.naomimyselfandi.xanaduwars.account.value.Plaintext;
import io.github.naomimyselfandi.xanaduwars.account.value.Username;
import io.github.naomimyselfandi.xanaduwars.util.Id;

import java.util.Optional;

/// A service that loads users for authentication.
public interface AuthService {

    /// Fetch the authenticated user's details, if any.
    Optional<UserDetailsDto> loadForAuthenticatedUser();

    /// Find a user by their ID.
    Optional<UserDetailsDto> loadUserById(Id<Account> id);

    /// Find a user by their username.
    Optional<UserDetailsDto> loadUserByName(Username username);

    /// Find a user by their username.
    Optional<UserDetailsDto> loadUserByEmailAddress(EmailAddress emailAddress);

    /// Find a user by their credentials.
    Optional<UserDetailsDto> loadUserByCredentials(Username username, Plaintext password);

    /// Find a user by their credentials.
    Optional<UserDetailsDto> loadUserByRefreshToken(String refreshToken);

    /// Set an account's "Remember Me" flag.
    void setRememberMe(Id<Account> accountId, boolean rememberMe);

}
