package io.github.naomimyselfandi.xanaduwars.auth.service;

import io.github.naomimyselfandi.xanaduwars.account.value.Plaintext;
import io.github.naomimyselfandi.xanaduwars.account.value.Username;
import io.github.naomimyselfandi.xanaduwars.auth.dto.UserDetailsDto;

import java.util.Optional;

/// A service that can retrieve the currently authenticated account.
public interface AuthService {

    /// Retrieve the currently authenticated account, if any.
    Optional<UserDetailsDto> tryGet();

    /// Find an account by its username and authentication secret.
    Optional<UserDetailsDto> find(Username username, Plaintext<?> secret);

}
