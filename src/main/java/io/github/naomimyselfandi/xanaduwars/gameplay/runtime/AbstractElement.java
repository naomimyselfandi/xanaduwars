package io.github.naomimyselfandi.xanaduwars.gameplay.runtime;

import io.github.naomimyselfandi.xanaduwars.gameplay.Element;
import io.github.naomimyselfandi.xanaduwars.gameplay.Player;
import io.github.naomimyselfandi.xanaduwars.gameplay.Ruleset;
import io.github.naomimyselfandi.xanaduwars.gameplay.Type;
import io.github.naomimyselfandi.xanaduwars.gameplay.queries.TagsQuery;
import io.github.naomimyselfandi.xanaduwars.gameplay.common.Name;
import io.github.naomimyselfandi.xanaduwars.gameplay.common.TagSet;
import io.github.naomimyselfandi.xanaduwars.gameplay.common.Tagged;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

@Getter
@EqualsAndHashCode(onParam_ = @Nullable)
abstract class AbstractElement<I, T extends Type> implements Tagged {

    // We can't extend Element because it's sealed.

    final AugmentedGameState gameState;
    final I id;

    @Getter(AccessLevel.NONE)
    final Ruleset ruleset;

    AbstractElement(AugmentedGameState gameState, I id) {
        this.gameState = gameState;
        this.id = id;
        this.ruleset = gameState().ruleset();
    }

    public abstract T type();

    @Override
    public Name name() {
        return type().name();
    }

    @Override
    public TagSet tags() {
        return gameState.evaluate(new TagsQuery((Element) this));
    }

    public abstract Optional<Player> owner();

    public boolean isAlly(Element that) {
        var thisOwner = this.owner().orElse(null);
        var thatOwner = that.owner().orElse(null);
        if (thisOwner == null || thatOwner == null) {
            return thisOwner == thatOwner;
        } else {
            return thisOwner.team() == thatOwner.team();
        }
    }

    @Override
    public String toString() {
        return "%s[%s]".formatted(id, type().name());
    }

}
