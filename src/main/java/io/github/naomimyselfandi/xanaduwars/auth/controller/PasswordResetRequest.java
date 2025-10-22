package io.github.naomimyselfandi.xanaduwars.auth.controller;

import io.github.naomimyselfandi.xanaduwars.account.value.Plaintext;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

/// A specialized DTO representing a password reset request.
public record PasswordResetRequest(@NotNull @Valid Plaintext password) {}
