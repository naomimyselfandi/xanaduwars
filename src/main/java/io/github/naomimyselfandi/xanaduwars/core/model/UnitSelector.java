package io.github.naomimyselfandi.xanaduwars.core.model;

/// A selector that can match units and/or unit types.
public interface UnitSelector {

    /// Test if a unit matches this selector.
    boolean test(Unit unit);

    /// Test if a unit type matches this selector.
    boolean test(UnitType unitType);

}
