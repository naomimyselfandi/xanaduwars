package io.github.naomimyselfandi.xanaduwars.core.ruleset.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.PositiveOrZero;

import java.io.Serializable;

/// The index of a commander.
@Embeddable
@SuppressWarnings("com.intellij.jpb.NoArgsConstructorInspection")
public record CommanderId(@Override @PositiveOrZero @Column(name = "commander") int index) implements Serializable {}
