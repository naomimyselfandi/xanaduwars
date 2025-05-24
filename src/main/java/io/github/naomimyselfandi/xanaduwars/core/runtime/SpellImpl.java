package io.github.naomimyselfandi.xanaduwars.core.runtime;

import io.github.naomimyselfandi.xanaduwars.core.Player;
import io.github.naomimyselfandi.xanaduwars.core.Spell;
import io.github.naomimyselfandi.xanaduwars.core.SpellType;
import lombok.Getter;

import java.util.Optional;

final class SpellImpl extends AbstractElement<SpellId, SpellType<?>> implements Spell {

    @Getter(onMethod_ = @Override)
    private final SpellType<?> type;
    private final Player owner;

    SpellImpl(AugmentedGameState gameState, SpellType<?> type, Player owner, int index) {
        super(gameState, new SpellId(owner.id().intValue(), index));
        this.type = type;
        this.owner = owner;
    }

    @Override
    public Optional<Player> owner() {
        return Optional.of(owner);
    }

}
