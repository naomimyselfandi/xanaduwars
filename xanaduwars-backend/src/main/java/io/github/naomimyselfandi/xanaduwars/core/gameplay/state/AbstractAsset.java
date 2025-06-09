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

    public abstract GameState gameState();

    public @Nullable Player owner() {
        return data.owner() instanceof PlayerId id ? gameState().player(id) : null;
    }

    public Stream<Rule> rules() {
        return Stream.ofNullable(owner()).flatMap(Player::rules);
    }

    public int hp() {
        return data.hp();
    }

    public Asset hp(int hp) {
        data.hp(Math.max(0, Math.min(100, hp)));
        return (Asset) this;
    }

    public boolean isFriend(@Nullable Actor actor) {
        return Optional.ofNullable(owner()).filter(owner -> owner.isFriend(actor)).isPresent();
    }

    public boolean isFoe(@Nullable Actor actor) {
        return Optional.ofNullable(owner()).filter(owner -> owner.isFoe(actor)).isPresent();
    }

    public boolean isSelf(@Nullable Actor actor) {
        return Optional.ofNullable(owner()).filter(owner -> owner.isSelf(actor)).isPresent();
    }

    public int vision() {
        var self = (Asset) this;
        return gameState().evaluate(new VisionRangeQuery(self));
    }

    public boolean canSee(Tile tile) {
        var self = (Asset) this;
        return gameState().evaluate(new VisionCheckQuery(self, tile));
    }

}
