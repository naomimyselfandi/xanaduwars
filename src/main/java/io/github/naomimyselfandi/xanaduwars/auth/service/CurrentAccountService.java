package io.github.naomimyselfandi.xanaduwars.auth.service;

import io.github.naomimyselfandi.xanaduwars.account.entity.Account;

import java.util.Optional;

/// A service that can retrieve the currently authenticated account.
public interface CurrentAccountService {

    /// Retrieve the currently authenticated account, if any.
    Optional<Account> tryGet();

    /// Retrieve the currently authenticated account.
    /// @throws org.springframework.security.access.AccessDeniedException if
    /// there is no authenticated account.
    Account get();

}
