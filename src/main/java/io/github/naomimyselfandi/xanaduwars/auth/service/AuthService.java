package io.github.naomimyselfandi.xanaduwars.auth.service;

import io.github.naomimyselfandi.xanaduwars.account.entity.Account;
import io.github.naomimyselfandi.xanaduwars.account.value.PlaintextPassword;
import io.github.naomimyselfandi.xanaduwars.account.value.Username;
import io.github.naomimyselfandi.xanaduwars.auth.dto.UserDetailsDto;
import io.github.naomimyselfandi.xanaduwars.util.Id;

import java.util.Optional;

/// A service that can retrieve the currently authenticated account.
public interface AuthService {

    /// Retrieve the currently authenticated account, if any.
    Optional<UserDetailsDto> tryGet();

    /// Retrieve the currently authenticated account.
    /// @throws org.springframework.web.server.ResponseStatusException with a
    /// 401 status if there is no authenticated account.
    UserDetailsDto get();

    /// Retrieve the ID of the currently authenticated account.
    /// @throws org.springframework.web.server.ResponseStatusException with a
    /// 401 status if there is no authenticated account.
    Id<Account> getId();

    /// Find an account by its username and authentication secret.
    Optional<UserDetailsDto> find(Username username, PlaintextPassword secret);

}
