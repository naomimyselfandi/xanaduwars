package io.github.naomimyselfandi.xanaduwars.auth.dto;

import io.github.naomimyselfandi.xanaduwars.account.value.EmailAddress;
import io.github.naomimyselfandi.xanaduwars.account.value.PlaintextPassword;
import io.github.naomimyselfandi.xanaduwars.account.value.Username;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

/// A specialized DTO representing a request for a new account.
public record Registration(
        @NotNull @Valid Username username,
        @NotNull @Valid EmailAddress emailAddress,
        @NotNull @Valid PlaintextPassword password
) {}
