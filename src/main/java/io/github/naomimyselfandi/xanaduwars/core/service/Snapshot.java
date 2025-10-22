package io.github.naomimyselfandi.xanaduwars.core.service;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;

/// A JSON snapshot of a game state.
@Embeddable
@SuppressWarnings("com.intellij.jpb.NoArgsConstructorInspection")
public record Snapshot(@Column(name = "snapshot", columnDefinition = "JSONB") String json) implements Serializable {}
