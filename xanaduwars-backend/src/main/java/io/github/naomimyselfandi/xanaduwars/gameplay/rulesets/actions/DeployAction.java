package io.github.naomimyselfandi.xanaduwars.gameplay.rulesets.actions;

import io.github.naomimyselfandi.xanaduwars.gameplay.*;
import io.github.naomimyselfandi.xanaduwars.gameplay.value.Execution;
import io.github.naomimyselfandi.xanaduwars.gameplay.value.Resource;
import io.github.naomimyselfandi.xanaduwars.gameplay.queries.UnitCostCalculation;
import io.github.naomimyselfandi.xanaduwars.gameplay.value.Name;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.stream.Stream;

/// An action that deploys a unit from a tile.
public record DeployAction(@Override @NotNull @Valid Name name) implements SimpleActionMixin<Tile, UnitType> {

    @Override
    public Stream<UnitType> enumerateTargets(GameState gameState) {
        return gameState.ruleset().unitTypes().stream();
    }

    @Override
    public Execution execute(Tile tile, UnitType unitType) {
        tile.createUnit(unitType, tile.owner().orElseThrow());
        return Execution.SUCCESSFUL;
    }

    @Override
    public boolean test(Tile tile, UnitType unitType) {
        return tile.unit().isEmpty() && tile.deploymentRoster().contains(unitType);
    }

    @Override
    public int cost(Resource resource, Tile tile, UnitType unitType) {
        return tile.gameState().evaluate(new UnitCostCalculation(tile, unitType, resource));
    }

}
