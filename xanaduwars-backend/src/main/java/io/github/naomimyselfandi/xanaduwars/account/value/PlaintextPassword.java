package io.github.naomimyselfandi.xanaduwars.account.value;

import com.fasterxml.jackson.annotation.JsonValue;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;

/// A *raw, unhashed* password. Use with caution!
public record PlaintextPassword(@NotNull @Length(min = 12) String text) implements Plaintext<Password> {

    @Override
    public Password toHash(String hashedValue) {
        return new Password(hashedValue);
    }

    @Override
    @JsonValue // To be safe
    public String toString() {
        return "************";
    }

}
