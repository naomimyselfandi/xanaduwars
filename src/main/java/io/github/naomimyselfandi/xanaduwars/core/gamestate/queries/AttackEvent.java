package io.github.naomimyselfandi.xanaduwars.core.gamestate.queries;

import io.github.naomimyselfandi.xanaduwars.core.gamestate.Asset;
import io.github.naomimyselfandi.xanaduwars.core.gamestate.Unit;
import io.github.naomimyselfandi.xanaduwars.core.ruleset.Weapon;
import io.github.naomimyselfandi.xanaduwars.core.scripting.SimpleEvent;
import io.github.naomimyselfandi.xanaduwars.util.ExcludeFromCoverageReport;

/// An event indicating that a unit is attacking with a weapon.
@ExcludeFromCoverageReport // Trivial record and not naturally used in any unit tests
public record AttackEvent(Unit subject, Weapon weapon, Asset target, boolean counter) implements SimpleEvent {}
