package io.github.naomimyselfandi.xanaduwars.email.value;

import io.github.naomimyselfandi.xanaduwars.account.dto.UserDetailsDto;
import io.github.naomimyselfandi.xanaduwars.auth.value.PasswordResetToken;

/// The content of an account activation email.
public record ActivationContent(UserDetailsDto account, PasswordResetToken token) implements EmailContent {}
