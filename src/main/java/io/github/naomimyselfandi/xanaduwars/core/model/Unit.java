package io.github.naomimyselfandi.xanaduwars.core.model;

import java.util.List;

/// A unit in a game.
public non-sealed interface Unit extends Node, Actor {

    /// Get this unit's type.
    UnitType getType();

    /// Set this unit's type.
    Unit setType(UnitType type);

    /// Get any tags that apply to this unit.
    List<UnitTag> getTags();

    /// Get this unit's owner.
    Player getOwner();

    /// Set this unit's owner.
    Unit setOwner(Player owner);

    /// Get this unit's location. This is usually a tile, but may be another
    /// unit that is transporting this one.
    Node getLocation();

    /// Set this unit's location. This is usually a tile, but may be another
    /// unit that is transporting this one.
    Unit setLocation(Node node);

    /// Get this unit's speed.
    int getSpeed();

    /// Get this unit's perception range.
    int getPerception();

    /// Check if this unit perceives a tile or another unit.
    boolean perceives(Node node);

    /// Get this unit's maximum HP.
    int getMaxHp();

    /// Get this unit's current HP.
    int getHp();

    /// Set this unit's current HP.
    Unit setHp(int hp);

    /// Get this unit's HP as a percentage.
    double getHpPercent();

    /// Set this unit's HP as a percentage.
    Unit setHpPercent(double hpPercent);

    /// Check if this unit is alive.
    boolean isAlive();

    /// Check if this unit is still being built.
    boolean isUnderConstruction();

    /// Set whether this unit is still being built.
    Unit setUnderConstruction(boolean underConstruction);

    /// Get the supply cost paid to deploy this unit.
    int getSupplyCost();

    /// Get the aether cost paid to deploy this unit.
    int getAetherCost();

    /// Get the unit tags this unit can carry.
    UnitSelector getHangar();

    /// Get any weapons this unit carries.
    List<Weapon> getWeapons();

}
