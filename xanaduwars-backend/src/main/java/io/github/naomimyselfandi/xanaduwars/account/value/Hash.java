package io.github.naomimyselfandi.xanaduwars.account.value;

/// A hashed password or API key.
public sealed interface Hash extends Secret permits APIKey, Password {}
