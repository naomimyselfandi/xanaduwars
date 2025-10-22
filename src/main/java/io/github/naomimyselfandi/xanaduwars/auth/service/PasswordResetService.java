package io.github.naomimyselfandi.xanaduwars.auth.service;

import io.github.naomimyselfandi.xanaduwars.account.dto.UserDetailsDto;
import io.github.naomimyselfandi.xanaduwars.account.value.EmailAddress;
import io.github.naomimyselfandi.xanaduwars.account.value.Plaintext;
import io.github.naomimyselfandi.xanaduwars.auth.value.PasswordResetToken;
import jakarta.validation.Valid;

import java.util.Optional;

/// A service that provides password reset functionality.
public interface PasswordResetService {

    /// Create a password reset token.
    void createResetTokenForUserRequest(EmailAddress emailAddress);

    /// Create a password reset token for a newly registered account.
    void createResetTokenForNewAccount(UserDetailsDto account);

    /// Validate a password reset token.
    Optional<UserDetailsDto> validateResetToken(PasswordResetToken token);

    /// Reset a user's password.
    void resetPassword(PasswordResetToken token, @Valid Plaintext password);

}
