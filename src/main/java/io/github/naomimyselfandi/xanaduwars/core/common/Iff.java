package io.github.naomimyselfandi.xanaduwars.core.common;

import com.fasterxml.jackson.annotation.JsonValue;

/// A team-based target filter.
public enum Iff implements TargetFilter {

    OWN, ALLY, ENEMY, NEUTRAL;

    @Override
    @JsonValue
    public String toString() {
        return switch (this) {
            case OWN -> "Own";
            case ALLY -> "Ally";
            case ENEMY -> "Enemy";
            case NEUTRAL -> "Neutral";
        };
    }

}
