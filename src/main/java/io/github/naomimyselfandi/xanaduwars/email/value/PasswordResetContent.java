package io.github.naomimyselfandi.xanaduwars.email.value;

import io.github.naomimyselfandi.xanaduwars.account.dto.UserDetailsDto;
import io.github.naomimyselfandi.xanaduwars.auth.value.PasswordResetToken;

/// The content of a password reset email.
public record PasswordResetContent(UserDetailsDto account, PasswordResetToken token) implements EmailContent {}
