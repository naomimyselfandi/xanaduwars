package io.github.naomimyselfandi.xanaduwars.auth.controller;

import io.github.naomimyselfandi.xanaduwars.account.value.EmailAddress;
import io.github.naomimyselfandi.xanaduwars.account.value.Username;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

/// A specialized DTO representing a registration request.
public record RegistrationRequest(@NotNull @Valid Username username, @NotNull @Valid EmailAddress emailAddress) {}
