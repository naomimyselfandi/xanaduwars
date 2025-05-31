package io.github.naomimyselfandi.xanaduwars.account.service;

import io.github.naomimyselfandi.xanaduwars.account.value.AccountReference;

/// An access guard that controls access to accounts.
public interface AccountGuard {

    /// Check if a reference refers to the currently authenticated account.
    boolean isCurrentAccount(AccountReference reference);

}
