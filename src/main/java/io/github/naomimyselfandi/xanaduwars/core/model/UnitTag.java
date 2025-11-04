package io.github.naomimyselfandi.xanaduwars.core.model;

import com.fasterxml.jackson.annotation.JsonValue;

/// A tag that categorizes units.
public record UnitTag(@JsonValue String name) implements UnitSelector {

    @Override
    public boolean test(Unit unit) {
        return unit.getTags().contains(this);
    }

    @Override
    public boolean test(UnitType unitType) {
        return unitType.getTags().contains(this);
    }

}
