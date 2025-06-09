package io.github.naomimyselfandi.xanaduwars.core.gameplay.state;

import io.github.naomimyselfandi.xanaduwars.core.gameplay.entity.PlayerData;
import io.github.naomimyselfandi.xanaduwars.core.gameplay.queries.DefeatEvent;
import io.github.naomimyselfandi.xanaduwars.core.ruleset.model.Action;
import io.github.naomimyselfandi.xanaduwars.core.ruleset.model.Commander;
import io.github.naomimyselfandi.xanaduwars.core.ruleset.model.Ruleset;
import io.github.naomimyselfandi.xanaduwars.core.ruleset.model.Tag;
import io.github.naomimyselfandi.xanaduwars.core.scripting.Rule;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Stream;

@RequiredArgsConstructor
@EqualsAndHashCode(onParam_ = @Nullable)
final class PlayerImpl implements Player {

    private final PlayerData data;

    @Getter(onMethod_ = @Override)
    private final GameState gameState;

    private final Ruleset ruleset;

    @Override
    public PlayerId id() {
        return data.id();
    }

    @Override
    public Team team() {
        return data.team();
    }

    @Override
    public Commander commander() {
        return ruleset.commander(data.commander());
    }

    @Override
    public Set<? extends Tag> tags() {
        return commander().tags();
    }

    @Override
    public @Unmodifiable List<SpellSlot> spellSlots() {
        return data
                .spellSlots()
                .slots()
                .stream()
                .<SpellSlot>map(data -> new SpellSlotImpl(data, ruleset))
                .toList();
    }

    @Override
    public Stream<Unit> units() {
        return gameState.units().filter(unit -> equals(unit.owner()));
    }

    @Override
    public Stream<Structure> structures() {
        return gameState
                .tiles()
                .stream()
                .map(Tile::structure)
                .filter(Objects::nonNull)
                .filter(structure -> equals(structure.owner()));
    }

    @Override
    public boolean canSee(@Nullable Physical element) {
        return switch (element) {
            case Asset asset when asset.owner() instanceof Player owner && team().equals(owner.team()) -> true;
            case Asset asset -> canSee(asset.tile());
            case Tile tile -> Stream.concat(units(), structures()).anyMatch(it -> it.canSee(tile));
            case null -> false;
        };
    }

    @Override
    public boolean defeated() {
        return data.defeated();
    }

    @Override
    public Player defeat() {
        data.defeated(true);
        gameState.evaluate(new DefeatEvent(this));
        return this;
    }

    @Override
    public int supplies() {
        return data.supplies();
    }

    @Override
    public Player supplies(int supplies) {
        data.supplies(supplies);
        return this;
    }

    @Override
    public int aether() {
        return data.aether();
    }

    @Override
    public Player aether(int aether) {
        data.aether(aether);
        return this;
    }

    @Override
    public int focus() {
        return data.focus();
    }

    @Override
    public Player focus(int focus) {
        data.focus(focus);
        return this;
    }

    @Override
    public Player owner() {
        return this;
    }

    @Override
    public boolean isSelf(@Nullable Actor actor) {
        return actor instanceof Actor it && equals(it.owner());
    }

    @Override
    public boolean isFoe(@Nullable Actor actor) {
        return actor instanceof Actor it && it.owner() instanceof Player owner && !team().equals(owner.team());
    }

    @Override
    public boolean isFriend(@Nullable Actor actor) {
        return actor instanceof Actor it && it.owner() instanceof Player owner && team().equals(owner.team());
    }

    @Override
    public @Unmodifiable List<Action> actions() {
        return Stream.of(
                spellSlots().stream().map(SpellSlot::spell),
                ruleset.commonPlayerActions().stream()
        ).<Action>flatMap(Function.identity()).toList();
    }

    @Override
    public Stream<Rule> rules() {
        var spells = spellSlots().stream().filter(it -> it.timesCastThisTurn() > 0).map(SpellSlot::spell);
        return Stream.concat(Stream.of(commander()), spells);
    }

    @Override
    public String toString() {
        return "Player[id=%d, commander=%s]".formatted(data.id().playerId(), commander());
    }

}
