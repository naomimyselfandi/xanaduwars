package io.github.naomimyselfandi.xanaduwars.core.gamestate.internal;

import io.github.naomimyselfandi.xanaduwars.core.common.StructureTag;
import io.github.naomimyselfandi.xanaduwars.core.gamestate.*;
import io.github.naomimyselfandi.xanaduwars.core.gamestate.entity.StructureData;
import io.github.naomimyselfandi.xanaduwars.core.gamestate.queries.StructureDestructionEvent;
import io.github.naomimyselfandi.xanaduwars.core.gamestate.queries.StructureTagQuery;
import io.github.naomimyselfandi.xanaduwars.core.gamestate.queries.VisionRangeQuery;
import io.github.naomimyselfandi.xanaduwars.core.ruleset.Action;
import io.github.naomimyselfandi.xanaduwars.core.ruleset.StructureType;
import io.github.naomimyselfandi.xanaduwars.core.scripting.Rule;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

record StructureImpl(StructureData data, @Getter GameState gameState, @Getter StructureId id) implements Structure {

    @Override
    public StructureType getType() {
        return gameState.getRuleset().getStructureType(data.getTypeId());
    }

    @Override
    public Structure setType(StructureType type) {
        data.setTypeId(type.getId());
        gameState.invalidateCache();
        return this;
    }

    @Override
    public @Unmodifiable Set<StructureTag> getTags() {
        return gameState.evaluate(new StructureTagQuery(this));
    }

    @Override
    public int getVision() {
        return gameState.evaluate(new VisionRangeQuery(this));
    }

    @Override
    public Hp getHp() {
        return data.getHp();
    }

    @Override
    public Asset setHp(Hp hp) {
        data.setHp(hp);
        if (hp.equals(Hp.ZERO)) {
            gameState.evaluate(new StructureDestructionEvent(this));
        } else {
            gameState.invalidateCache();
        }
        return this;
    }

    @Override
    public Tile getTile() {
        return gameState.getTiles().get(id.tileId());
    }

    @Override
    public boolean isIncomplete() {
        return data.isIncomplete();
    }

    @Override
    public Structure setIncomplete(boolean incomplete) {
        data.setIncomplete(incomplete);
        gameState.invalidateCache();
        return this;
    }

    @Override
    public @Nullable Player getOwner() {
        return Optional
                .ofNullable(data.getPlayerId())
                .map(PlayerId::playerId)
                .map(gameState.getPlayers()::get)
                .orElse(null);
    }

    @Override
    public Asset setOwner(@Nullable Player owner) {
        data.setPlayerId(owner == null ? null : owner.getId());
        gameState.invalidateCache();
        return this;
    }

    @Override
    public @Unmodifiable List<Action> getActions() {
        return isIncomplete() ? List.of() : getType().getActions();
    }

    @Override
    public List<Rule> getRules() {
        return Stream.concat(
                Stream.of(getType()),
                Stream.ofNullable(getOwner()).map(Player::getRules).flatMap(List::stream)
        ).toList();
    }

}
