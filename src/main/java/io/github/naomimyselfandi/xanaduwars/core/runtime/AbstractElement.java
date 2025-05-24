package io.github.naomimyselfandi.xanaduwars.core.runtime;

import io.github.naomimyselfandi.xanaduwars.core.Element;
import io.github.naomimyselfandi.xanaduwars.core.Ruleset;
import io.github.naomimyselfandi.xanaduwars.core.Type;
import io.github.naomimyselfandi.xanaduwars.core.queries.TagsQuery;
import io.github.naomimyselfandi.xanaduwars.core.wrapper.Name;
import io.github.naomimyselfandi.xanaduwars.core.wrapper.TagSet;
import io.github.naomimyselfandi.xanaduwars.core.wrapper.Tagged;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;

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
        var self = (Element) this;
        return gameState.evaluate(new TagsQuery(self), type().tags());
    }

    @Override
    public String toString() {
        return "%s[%s]".formatted(id, type().name());
    }

}
