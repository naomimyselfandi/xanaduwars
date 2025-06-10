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
    public PlayerId getId() {
        return data.getId();
    }

    @Override
    public Team getTeam() {
        return data.getTeam();
    }

    @Override
    public Commander getCommander() {
        return ruleset.getCommander(data.getCommander());
    }

    @Override
    public Set<? extends Tag> getTags() {
        return getCommander().getTags();
    }

    @Override
    public @Unmodifiable List<SpellSlot> getSpellSlots() {
        return data
                .getSpellSlots()
                .slots()
                .stream()
                .<SpellSlot>map(data -> new SpellSlotImpl(data, ruleset))
                .toList();
    }

    @Override
    public Stream<Unit> getUnits() {
        return gameState.getUnits().filter(unit -> equals(unit.getOwner()));
    }

    @Override
    public Stream<Structure> getStructures() {
        return gameState
                .getTiles()
                .stream()
                .map(Tile::getStructure)
                .filter(Objects::nonNull)
                .filter(structure -> equals(structure.getOwner()));
    }

    @Override
    public boolean canSee(@Nullable Physical element) {
        return switch (element) {
            case Asset asset when asset.getOwner() instanceof Player owner && getTeam().equals(owner.getTeam()) -> true;
            case Asset asset -> canSee(asset.getTile());
            case Tile tile -> Stream.concat(getUnits(), getStructures()).anyMatch(it -> it.canSee(tile));
            case null -> false;
        };
    }

    @Override
    public boolean isDefeated() {
        return data.isDefeated();
    }

    @Override
    public Player defeat() {
        data.setDefeated(true);
        gameState.evaluate(new DefeatEvent(this));
        return this;
    }

    @Override
    public int getSupplies() {
        return data.getSupplies();
    }

    @Override
    public Player setSupplies(int supplies) {
        data.setSupplies(supplies);
        return this;
    }

    @Override
    public int getAether() {
        return data.getAether();
    }

    @Override
    public Player setAether(int aether) {
        data.setAether(aether);
        return this;
    }

    @Override
    public int getFocus() {
        return data.getFocus();
    }

    @Override
    public Player setFocus(int focus) {
        data.setFocus(focus);
        return this;
    }

    @Override
    public Player getOwner() {
        return this;
    }

    @Override
    public boolean isSelf(@Nullable Actor actor) {
        return actor instanceof Actor it && equals(it.getOwner());
    }

    @Override
    public boolean isFoe(@Nullable Actor actor) {
        return actor instanceof Actor it && it.getOwner() instanceof Player owner && !getTeam().equals(owner.getTeam());
    }

    @Override
    public boolean isFriend(@Nullable Actor actor) {
        return actor instanceof Actor it && it.getOwner() instanceof Player owner && getTeam().equals(owner.getTeam());
    }

    @Override
    public @Unmodifiable List<Action> getAction() {
        return Stream.of(
                getSpellSlots().stream().map(SpellSlot::getSpell),
                ruleset.getCommonPlayerActions().stream()
        ).<Action>flatMap(Function.identity()).toList();
    }

    @Override
    public Stream<Rule> rules() {
        var spells = getSpellSlots().stream().filter(it -> it.getCastsThisTurn() > 0).map(SpellSlot::getSpell);
        return Stream.concat(Stream.of(getCommander()), spells);
    }

    @Override
    public String toString() {
        return "Player[id=%d, commander=%s]".formatted(data.getId().playerId(), getCommander());
    }

}
