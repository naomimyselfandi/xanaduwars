package io.github.naomimyselfandi.xanaduwars.core.gameplay.state;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;

/// A turn number.
@Embeddable
@SuppressWarnings("com.intellij.jpb.NoArgsConstructorInspection")
public record Turn(@Column(name = "turn") int turn) implements Serializable {}
