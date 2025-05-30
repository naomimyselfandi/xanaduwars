package io.github.naomimyselfandi.xanaduwars.account.value;

import com.fasterxml.jackson.annotation.JsonValue;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;

/// A hashed API key.
@Embeddable
@SuppressWarnings("com.intellij.jpb.NoArgsConstructorInspection")
public record APIKey(@Column(name = "secret_hash") @NotNull String text) implements Hash {

    @Override
    @JsonValue // To be safe
    public String toString() {
        return "************";
    }

}
