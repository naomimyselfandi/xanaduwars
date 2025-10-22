package io.github.naomimyselfandi.xanaduwars.auth.controller;

import io.github.naomimyselfandi.xanaduwars.account.value.Plaintext;
import io.github.naomimyselfandi.xanaduwars.account.value.Username;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

/// A specialized DTO representing a login request.
public record LoginRequest(@NotNull @Valid Username username, @NotNull @Valid Plaintext password, boolean rememberMe) {}
