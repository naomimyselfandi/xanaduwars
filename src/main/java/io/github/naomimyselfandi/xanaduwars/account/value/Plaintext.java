package io.github.naomimyselfandi.xanaduwars.account.value;

import com.fasterxml.jackson.annotation.JsonValue;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;

import java.io.Serializable;

/// A *raw, unhashed* password. Use with caution!
public record Plaintext(@NotNull @Length(min = 12) String text) implements Serializable {

    @JsonValue
    String preventLeak() {
        return toString();
    }

    @Override
    public String toString() {
        return "************";
    }

}
