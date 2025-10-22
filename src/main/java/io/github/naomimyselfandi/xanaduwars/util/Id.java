package io.github.naomimyselfandi.xanaduwars.util;

import com.fasterxml.jackson.annotation.JsonValue;
import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.UUID;

/// An ID of some database entity.
/// @param <T> The type of entity identified by this ID.
@Embeddable
@SuppressWarnings({"unused", "com.intellij.jpb.NoArgsConstructorInspection"})
public record Id<T>(@JsonValue UUID id) implements Serializable {

    @Override
    public String toString() {
        return id.toString();
    }

}
