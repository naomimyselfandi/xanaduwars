package io.github.naomimyselfandi.xanaduwars.auth.value;

import com.fasterxml.jackson.annotation.JsonValue;

/// A token used to reset a password.
public record PasswordResetToken(@JsonValue String token) {

    @Override
    public String toString() {
        return "PasswordResetToken[token=************]";
    }

}
