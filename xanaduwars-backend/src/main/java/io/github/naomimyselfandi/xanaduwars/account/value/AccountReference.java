package io.github.naomimyselfandi.xanaduwars.account.value;

/// A reference to an account. This can either be a specific account or the
/// currently authenticated account.
public sealed interface AccountReference permits AccountIdReference, CurrentAccountReference {

    /// A reference to the currently authenticated account.
    CurrentAccountReference CURRENT_ACCOUNT = new CurrentAccountReference();

}
