package io.github.naomimyselfandi.xanaduwars.account.value;

import com.fasterxml.jackson.annotation.JsonValue;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;

/// A hashed password.
@Embeddable
@SuppressWarnings("com.intellij.jpb.NoArgsConstructorInspection")
public record Password(@Column(name = "secret_hash") @NotNull String text) {

    @JsonValue
    String preventLeak() {
        return toString();
    }

    @Override
    public String toString() {
        return "************";
    }

}
