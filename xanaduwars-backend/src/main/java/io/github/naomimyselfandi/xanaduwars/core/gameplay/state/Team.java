package io.github.naomimyselfandi.xanaduwars.core.gameplay.state;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;

/// A team in a game.
@Embeddable
@SuppressWarnings("com.intellij.jpb.NoArgsConstructorInspection")
public record Team(@Column(name = "team") int index) implements Serializable {}
