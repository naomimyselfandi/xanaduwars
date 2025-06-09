package io.github.naomimyselfandi.xanaduwars.core.gameplay.entity;

import com.fasterxml.jackson.annotation.JsonValue;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.GeneratedValue;

import java.io.Serializable;
import java.util.UUID;

/// The ID of a game state.
@Embeddable
@SuppressWarnings("com.intellij.jpb.NoArgsConstructorInspection")
public record GameStateId(@GeneratedValue @Column @JsonValue UUID id) implements Serializable {

    @Override
    public String toString() {
        return id.toString();
    }

}
