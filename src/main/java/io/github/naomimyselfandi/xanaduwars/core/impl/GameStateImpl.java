package io.github.naomimyselfandi.xanaduwars.core.impl;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.github.naomimyselfandi.xanaduwars.core.message.AbstractMessageBus;
import io.github.naomimyselfandi.xanaduwars.core.message.Rule;
import io.github.naomimyselfandi.xanaduwars.core.messages.TurnStartedEvent;
import io.github.naomimyselfandi.xanaduwars.core.model.*;
import io.github.naomimyselfandi.xanaduwars.core.script.Script;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AccessLevel;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Stream;

@Getter(onMethod_ = @Override)
class GameStateImpl extends AbstractMessageBus implements GameState {

    @Getter(AccessLevel.NONE)
    private final Map<String, Object> constants = new HashMap<>();

    @JsonSerialize(using = VersionNumberSerializer.class)
    private final Version version;

    private final @PositiveOrZero boolean redacted;

    private final @Positive int width, height;

    private final List<Player> players;

    private final List<Tile> tiles;

    private @PositiveOrZero int turn;

    GameStateImpl(
            Version version,
            boolean redacted,
            int width,
            int height,
            List<? extends Player> players,
            List<? extends Tile> tiles,
            int turn
    ) {
        this.version = version;
        this.redacted = redacted;
        this.width = width;
        this.height = height;
        this.players = List.copyOf(players);
        this.tiles = List.copyOf(tiles);
        this.turn = turn;
    }

    @Override
    public @Nullable Object lookup(String name) {
        if (constants.containsKey(name)) {
            return constants.get(name);
        }
        var value = version.lookup(name);
        if (value instanceof Script script) {
            value = script.executeAsLibrary(this);
        }
        constants.put(name, value);
        return value;
    }

    @Override
    public GameState setTurn(int turn) {
        this.turn = turn;
        var player = getActivePlayer();
        Stream.concat(Stream.of(player), player.getUnits()).forEach(it -> {
            it.setActiveAbilities(List.of());
            dispatch(new TurnStartedEvent(it));
        });
        return this;
    }

    @Override
    @JsonIgnore
    public Player getActivePlayer() {
        return players.get(turn % players.size());
    }

    @Override
    public List<Player> getPlayers() {
        return List.copyOf(players);
    }

    @Override
    public Player getPlayer(int position) {
        return players.get(position);
    }

    @Override
    public List<Tile> getTiles() {
        return List.copyOf(tiles);
    }

    @Override
    public @Nullable Tile getTile(int x, int y) {
        return (x < 0 || y < 0 || x >= width || y >= height) ? null : tiles.get(y * width + x);
    }

    @Override
    @JsonIgnore
    public Stream<Unit> getUnits() {
        interface Helper {
            static Stream<Unit> stream(@Nullable Unit unit) {
                return (unit == null) ? Stream.empty() : Stream.concat(Stream.of(unit), stream(unit.getUnit()));
            }
        }
        return tiles.stream().map(Tile::getUnit).flatMap(Helper::stream);
    }

    @Override
    public boolean submitPlayerCommand(List<Command> commands) throws CommandException {
        return submitCommand(getActivePlayer(), commands);
    }

    @Override
    public boolean submitUnitCommand(int x, int y, List<Command> commands) throws CommandException {
        if (getTile(x, y) instanceof Tile tile) {
            return submitCommand(tile.getUnit(), commands);
        } else {
            throw new CommandException("Invalid coordinates for command.");
        }
    }

    @Override
    protected Stream<Rule> getGlobalRules() {
        return version.getGlobalRules().stream();
    }

    GameState initialize() {
        for (var player : players) {
            player.initialize(this);
        }
        for (var tile : tiles) {
            tile.initialize(this);
        }
        return this;
    }

    private boolean submitCommand(@Nullable Actor actor, List<Command> commands) throws CommandException {
        if (commands.isEmpty()) {
            throw new CommandException("At least one command must be given.");
        }
        record InterpretedCommand(Ability ability, Object target) {}
        if (actor == null || !actor.isReady()) {
            throw new CommandException("Invalid recipient for command.");
        }
        var interpretedCommands = new ArrayList<InterpretedCommand>(commands.size());
        for (var command : commands) {
            if (lookup(command.abilityName()) instanceof Ability ability) {
                var unpackedTarget = ability.unpack(actor, command.target());
                interpretedCommands.add(new InterpretedCommand(ability, unpackedTarget));
            } else {
                throw new CommandException("Unknown ability '%s'.".formatted(command.abilityName()));
            }
        }
        for (var interpretedCommand : interpretedCommands) {
            var ability = interpretedCommand.ability;
            var target = interpretedCommand.target;
            ability.validate(actor, target);
            if (!ability.execute(actor, target)) {
                return false;
            }
        }
        return true;
    }

}
