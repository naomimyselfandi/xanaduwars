package io.github.naomimyselfandi.xanaduwars.account.value;

import com.fasterxml.jackson.annotation.JsonValue;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;

/// A *raw, unhashed* API key. Use with caution!
public record PlaintextAPIKey(@NotNull @Length(min = 12) String text) implements Plaintext<APIKey> {

    @Override
    public APIKey toHash(String hashedValue) {
        return new APIKey(hashedValue);
    }

    @Override
    @JsonValue // To be safe
    public String toString() {
        return "************";
    }

}
