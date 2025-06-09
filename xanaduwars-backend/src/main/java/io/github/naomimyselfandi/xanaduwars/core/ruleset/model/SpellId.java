package io.github.naomimyselfandi.xanaduwars.core.ruleset.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.PositiveOrZero;

import java.io.Serializable;

/// The index of a spell.
@Embeddable
@SuppressWarnings("com.intellij.jpb.NoArgsConstructorInspection")
public record SpellId(@Override @PositiveOrZero @Column(name = "spell") int index) implements Serializable {}
