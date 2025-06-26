package io.github.naomimyselfandi.xanaduwars.core.gamestate.internal;

import io.github.naomimyselfandi.xanaduwars.core.gamestate.*;
import io.github.naomimyselfandi.xanaduwars.core.gamestate.entity.PlayerData;
import io.github.naomimyselfandi.xanaduwars.core.gamestate.queries.GenericEvent;
import io.github.naomimyselfandi.xanaduwars.core.gamestate.queries.ResourceEvent;
import io.github.naomimyselfandi.xanaduwars.core.gamestate.queries.VisionCheckQuery;
import io.github.naomimyselfandi.xanaduwars.core.ruleset.Action;
import io.github.naomimyselfandi.xanaduwars.core.ruleset.Commander;
import io.github.naomimyselfandi.xanaduwars.core.ruleset.Spell;
import io.github.naomimyselfandi.xanaduwars.core.scripting.Rule;
import lombok.Getter;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

record PlayerImpl(PlayerData data, @Getter GameState gameState, @Getter PlayerId id, SpellSlotHelper spellSlotHelper)
        implements Player {

    @Override
    public Optional<Commander> getCommander() {
        return Optional.ofNullable(data.getCommanderId()).map(gameState.getRuleset()::getCommander);
    }

    @Override
    public Player setCommander(Commander commander) {
        data.setCommanderId(commander.getId());
        gameState.evaluate(new GenericEvent(this));
        return this;
    }

    @Override
    public Team getTeam() {
        return data.getTeam();
    }

    @Override
    public boolean canSee(Physical target) {
        return gameState.evaluate(new VisionCheckQuery(this, target));
    }

    @Override
    public @Unmodifiable List<SpellSlot> getSpellSlots() {
        return spellSlotHelper.getSpellSlots(gameState.getRuleset(), data);
    }

    @Override
    public @Unmodifiable List<Spell> getChosenSpells() {
        return spellSlotHelper.getChosenSpells(gameState.getRuleset(), data);
    }

    @Override
    public Player setChosenSpells(List<Spell> chosenSpells) {
        data.getChosenSpells().setSpellIds(chosenSpells.stream().map(Spell::getId).toList());
        gameState.evaluate(new GenericEvent(this));
        return this;
    }

    @Override
    public int getSupplies() {
        return data.getSupplies();
    }

    @Override
    public Player setSupplies(int supplies) {
        data.setSupplies(supplies);
        gameState.evaluate(new ResourceEvent(this));
        return this;
    }

    @Override
    public int getAether() {
        return data.getAether();
    }

    @Override
    public Player setAether(int aether) {
        data.setAether(aether);
        gameState.evaluate(new ResourceEvent(this));
        return this;
    }

    @Override
    public int getFocus() {
        return data.getFocus();
    }

    @Override
    public Player setFocus(int focus) {
        data.setFocus(focus);
        gameState.evaluate(new ResourceEvent(this));
        return this;
    }

    @Override
    public boolean isDefeated() {
        return data.isDefeated();
    }

    @Override
    public Player defeat() {
        data.setDefeated(true);
        gameState.evaluate(new GenericEvent(this));
        return this;
    }

    @Override
    public Stream<Structure> getStructures() {
        return gameState.getStructures().values().stream().filter(it -> equals(it.getOwner().orElse(null)));
    }

    @Override
    public Stream<Unit> getUnits() {
        return gameState.getUnits().values().stream().filter(it -> equals(it.getOwner().orElse(null)));
    }

    @Override
    public Optional<Player> getOwner() {
        return Optional.of(this);
    }

    @Override
    public @Unmodifiable List<Action> getActions() {
        var ruleset = gameState.getRuleset();
        return Stream.concat(
                getSpellSlots().stream().map(SpellSlot::getSpell).flatMap(Optional::stream),
                Stream.of(ruleset.getPassAction(), ruleset.getYieldAction())
        ).filter(Objects::nonNull).toList();
    }

    @Override
    public List<Rule> getRules() {
        return Stream.<Rule>concat(
                getCommander().stream(),
                getSpellSlots().stream().filter(SpellSlot::isActive).map(SpellSlot::getSpell).flatMap(Optional::stream)
        ).filter(Objects::nonNull).toList();
    }

}
