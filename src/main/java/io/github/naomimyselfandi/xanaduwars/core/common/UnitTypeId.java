package io.github.naomimyselfandi.xanaduwars.core.common;

import com.fasterxml.jackson.annotation.JsonValue;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.PositiveOrZero;

import java.io.Serializable;

/// The index of a unit type.
@Embeddable
@SuppressWarnings("com.intellij.jpb.NoArgsConstructorInspection")
public record UnitTypeId(@Override @PositiveOrZero @Column(name = "unit_type") @JsonValue int index)
        implements Serializable {}
