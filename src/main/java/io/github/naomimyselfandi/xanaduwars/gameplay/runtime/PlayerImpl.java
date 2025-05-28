package io.github.naomimyselfandi.xanaduwars.gameplay.runtime;

import io.github.naomimyselfandi.xanaduwars.gameplay.*;
import io.github.naomimyselfandi.xanaduwars.gameplay.common.Resource;
import io.github.naomimyselfandi.xanaduwars.gameplay.runtime.data.PlayerData;
import io.github.naomimyselfandi.xanaduwars.gameplay.queries.VisionQuery;
import io.github.naomimyselfandi.xanaduwars.gameplay.common.PlayerId;
import io.github.naomimyselfandi.xanaduwars.gameplay.common.SpellTypeId;
import lombok.Getter;
import org.jetbrains.annotations.Unmodifiable;

import java.util.*;
import java.util.stream.IntStream;
import java.util.stream.Stream;

final class PlayerImpl extends AbstractElement<PlayerId, Commander> implements Player {

    @Getter
    private final Commander type;
    private final PlayerData playerData;

    PlayerImpl(AugmentedGameState gameState, PlayerData data) {
        super(gameState, new PlayerId(data.playerId().intValue()));
        this.type = ruleset.commanders().get(data.commander().index());
        this.playerData = data;
    }

    @Override
    public int team() {
        return playerData.team();
    }

    @Override
    public boolean defeated() {
        return playerData.defeated();
    }

    @Override
    public void defeat() {
        playerData.defeated(true);
    }

    @Override
    public boolean canSee(Node node) {
        return gameState.evaluate(new VisionQuery(this, node));
    }

    @Override
    public Stream<Unit> units() {
        return gameState.units().stream().filter(u -> equals(u.owner().orElse(null)));
    }

    @Override
    public Stream<Tile> tiles() {
        return gameState.tiles().stream().flatMap(List::stream).filter(t -> equals(t.owner().orElse(null)));
    }

    @Override
    public @Unmodifiable Map<Resource, Integer> resources() {
        return playerData.resources();
    }

    @Override
    public void resource(Resource resource, int quantity) {
        var copy = new EnumMap<>(playerData.resources());
        copy.put(resource, quantity);
        playerData.resources(copy);
    }

    @Override
    public @Unmodifiable List<SpellType<?>> knownSpells() {
        return playerData
                .knownSpells()
                .stream()
                .map(SpellTypeId::index)
                .<SpellType<?>>map(ruleset.spellTypes()::get)
                .toList();
    }

    @Override
    public @Unmodifiable List<Spell> activeSpells() {
        var data = playerData.activeSpells();
        var types = ruleset.spellTypes();
        return IntStream
                .range(0, data.size())
                .<Spell>mapToObj(index -> {
                    var type = types.get(data.get(index).index());
                    return new SpellImpl(gameState, type, this, index);
                })
                .toList();
    }

    @Override
    public void addActiveSpell(SpellType<?> spellType) {
        var copy = new ArrayList<>(playerData.activeSpells());
        copy.add(spellType.id());
        playerData.activeSpells(copy);
    }

    @Override
    public void clearActiveSpells() {
        playerData.activeSpells(List.of());
    }

    @Override
    public Optional<Player> owner() {
        return Optional.of(this);
    }

}
