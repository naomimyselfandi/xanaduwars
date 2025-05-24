package io.github.naomimyselfandi.xanaduwars.core.runtime;

import io.github.naomimyselfandi.xanaduwars.core.GameState;
import io.github.naomimyselfandi.xanaduwars.core.Node;
import io.github.naomimyselfandi.xanaduwars.core.Unit;
import io.github.naomimyselfandi.xanaduwars.core.UnitType;
import io.github.naomimyselfandi.xanaduwars.core.data.UnitData;

public interface AugmentedGameState extends GameState {

    Unit createUnit(Node location, UnitType type);

    void destroyUnit(UnitData unitData);

}
