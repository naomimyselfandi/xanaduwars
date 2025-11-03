package io.github.naomimyselfandi.xanaduwars.core.impl;

import io.github.naomimyselfandi.xanaduwars.core.model.*;
import io.github.naomimyselfandi.xanaduwars.core.service.CopyMachine;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Predicate;

@Service
class CopyMachineImpl implements CopyMachine {

    private static final BiConsumer<Player, Player> NO_OP = (_, _) -> {};
    private static final Predicate<Unit> TRUE = _ -> true;

    @Override
    public GameState createCopy(GameState source) {
        return createCopy(source, TRUE, source.isRedacted()).initialize();
    }

    @Override
    public GameState createRedactedCopy(GameState source, Player viewpoint) {
        var copy = createCopy(source, viewpoint::perceives, true);
        var copyViewpoint = copy.getPlayer(viewpoint.getPosition());
        copy.getVersion().getRedactionPolicy().execute(copy, Map.of("viewpoint", copyViewpoint));
        return copy.initialize();
    }

    private GameStateImpl createCopy(
            GameState source,
            Predicate<Unit> unitFilter,
            boolean redacted
    ) {
        var players = source.getPlayers().stream().map(this::createCopy).toList();
        var tiles = source.getTiles().stream().map(it -> createCopy(it, unitFilter, players)).toList();
        return new GameStateImpl(
                source.getVersion(),
                redacted,
                source.getWidth(),
                source.getHeight(),
                players,
                tiles,
                source.getTurn()
        );
    }

    private Player createCopy(Player source) {
        var copy = new PlayerImpl();
        for (var field : PlayerImpl.Fields.values()) {
            var _ = switch (field) {
                case position -> copy.setPosition(source.getPosition());
                case team -> copy.setTeam(source.getTeam());
                case supplies -> copy.setSupplies(source.getSupplies());
                case aether -> copy.setAether(source.getAether());
                case focus -> copy.setFocus(source.getFocus());
                case commander -> copy.setCommander(source.getCommander());
                case abilities -> copy.setAbilities(source.getAbilities());
                case activeAbilities -> copy.setActiveAbilities(source.getActiveAbilities());
                case usedAbilities -> copy.setUsedAbilities(source.getUsedAbilities());
                case defeated -> copy.setDefeated(source.isDefeated());
            };
        }
        return copy;
    }

    private Tile createCopy(Tile source, Predicate<Unit> filter, List<Player> players) {
        var copy = new TileImpl();
        for (var field : TileImpl.Fields.values()) {
            var _ = switch (field) {
                case x -> copy.setX(source.getX());
                case y -> copy.setY(source.getY());
                case type -> copy.setType(source.getType());
            };
        }
        if (source.getUnit() instanceof Unit unit && filter.test(unit)) {
            copy.setUnit(createCopy(unit, filter, players));
        }
        return copy;
    }

    private Unit createCopy(Unit source, Predicate<Unit> filter, List<Player> players) {
        var copy = new UnitImpl();
        for (var field : UnitImpl.Fields.values()) {
            var _ = switch (field) {
                case type -> copy.setType(source.getType());
                case location -> null;
                case owner -> copy.setOwner(players.get(source.getOwner().getPosition()));
                case scaledHpPercent -> copy.setHpPercent(source.getHpPercent());
                case underConstruction -> copy.setUnderConstruction(source.isUnderConstruction());
                case activeAbilities -> copy.setActiveAbilities(source.getActiveAbilities());
            };
        }
        if (source.getUnit() instanceof Unit cargo && filter.test(cargo)) {
            copy.setUnit(createCopy(cargo, filter, players));
        }
        return copy;
    }

}
