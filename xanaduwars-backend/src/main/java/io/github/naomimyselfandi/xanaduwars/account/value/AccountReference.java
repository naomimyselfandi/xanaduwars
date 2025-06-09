package io.github.naomimyselfandi.xanaduwars.account.value;

import io.github.naomimyselfandi.xanaduwars.ext.ExcludeFromCoverageReport;

import java.io.Serializable;

/// A reference to an account. This can either be a specific account or the
/// currently authenticated account.
public sealed interface AccountReference extends Serializable permits AccountId, AccountReference.Me {

    /// A reference to the currently authenticated account.
    Me ME = new Me();

    /// A reference to the currently authenticated account.
    @ExcludeFromCoverageReport // False positive
    record Me() implements AccountReference {}

}
