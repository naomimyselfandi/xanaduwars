package io.github.naomimyselfandi.xanaduwars.core.gameplay.state;

import io.github.naomimyselfandi.xanaduwars.core.gameplay.entity.AssetData;
import io.github.naomimyselfandi.xanaduwars.core.gameplay.queries.VisionCheckQuery;
import io.github.naomimyselfandi.xanaduwars.core.gameplay.queries.VisionRangeQuery;
import io.github.naomimyselfandi.xanaduwars.core.scripting.Rule;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.stream.Stream;

@RequiredArgsConstructor
@EqualsAndHashCode(onParam_ = @Nullable)
abstract class AbstractAsset<T extends AssetData> {

    final T data;

    public abstract GameState getGameState();

    public @Nullable Player getOwner() {
        return data.getOwner() instanceof PlayerId id ? getGameState().getPlayer(id) : null;
    }

    public Stream<Rule> rules() {
        return Stream.ofNullable(getOwner()).flatMap(Player::rules);
    }

    public int getHp() {
        return data.getHp();
    }

    public Asset setHp(int hp) {
        data.setHp(Math.max(0, Math.min(100, hp)));
        return (Asset) this;
    }

    public boolean isFriend(@Nullable Actor actor) {
        return Optional.ofNullable(getOwner()).filter(owner -> owner.isFriend(actor)).isPresent();
    }

    public boolean isFoe(@Nullable Actor actor) {
        return Optional.ofNullable(getOwner()).filter(owner -> owner.isFoe(actor)).isPresent();
    }

    public boolean isSelf(@Nullable Actor actor) {
        return Optional.ofNullable(getOwner()).filter(owner -> owner.isSelf(actor)).isPresent();
    }

    public int getVision() {
        var self = (Asset) this;
        return getGameState().evaluate(new VisionRangeQuery(self));
    }

    public boolean canSee(Tile tile) {
        var self = (Asset) this;
        return getGameState().evaluate(new VisionCheckQuery(self, tile));
    }

}
