package io.github.naomimyselfandi.xanaduwars.account.value;

import java.util.UUID;

/// A reference to a specific account by its ID.
public record AccountIdReference(UUID id) implements AccountReference {}
