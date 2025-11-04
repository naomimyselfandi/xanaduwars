package io.github.naomimyselfandi.xanaduwars.core.model;

/// A placeholder unit selector that never matches.
public enum UnitSelectorPlaceholder implements UnitSelector {

    /// A placeholder unit selector that never matches.
    NONE;

    @Override
    public boolean test(Unit unit) {
        return false;
    }

    @Override
    public boolean test(UnitType unitType) {
        return false;
    }

}
