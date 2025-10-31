package io.github.naomimyselfandi.xanaduwars.core.service;

import io.github.naomimyselfandi.xanaduwars.util.NotCovered;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;

/// A JSON snapshot of a game state.
@Embeddable
@NotCovered // Trivial
@SuppressWarnings("com.intellij.jpb.NoArgsConstructorInspection")
public record Snapshot(@Column(name = "snapshot", columnDefinition = "JSONB") String json) implements Serializable {}
