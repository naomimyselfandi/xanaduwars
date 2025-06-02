package io.github.naomimyselfandi.xanaduwars.account.value;

import com.fasterxml.jackson.annotation.JsonValue;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.GeneratedValue;

import java.util.UUID;

/// A reference to a specific account by its ID.
@Embeddable
@SuppressWarnings("com.intellij.jpb.NoArgsConstructorInspection")
public record AccountId(@GeneratedValue @Column @JsonValue UUID id) implements AccountReference {

    @Override
    public String toString() {
        return id.toString();
    }

}
