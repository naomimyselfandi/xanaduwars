package io.github.naomimyselfandi.xanaduwars.auth.dto;

import io.github.naomimyselfandi.xanaduwars.account.value.PlaintextPassword;
import io.github.naomimyselfandi.xanaduwars.account.value.Username;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

/// A specialized DTO representing a login request.
public record LoginRequest(@NotNull @Valid Username username, @NotNull @Valid PlaintextPassword password) {}
