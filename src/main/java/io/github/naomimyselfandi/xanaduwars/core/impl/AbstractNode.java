package io.github.naomimyselfandi.xanaduwars.core.impl;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.github.naomimyselfandi.xanaduwars.core.messages.AnimationEvent;
import io.github.naomimyselfandi.xanaduwars.core.messages.UnitCreatedEvent;
import io.github.naomimyselfandi.xanaduwars.core.model.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

@NoArgsConstructor
abstract class AbstractNode extends AbstractElement {

    @Getter
    @JsonDeserialize(as = UnitImpl.class)
    private @Nullable Unit unit;

    @SuppressWarnings("UnusedReturnValue")
    public Node setUnit(@Nullable Unit unit) {
        var self = (Node) this;
        if (!Objects.equals(unit, this.unit)) {
            if (this.unit != null && unit != null) {
                throw new IllegalStateException("%s already has a unit!".formatted(this));
            }
            this.unit = unit;
            if (unit != null) {
                unit.setLocation(self);
            }
            clearQueryCache(); // don't really need this, but it simplifies testing
        }
        return self;
    }

    public Unit createUnit(UnitType type, Player owner) {
        var self = (Node) this;
        var unit = new UnitImpl();
        unit.setType(type).setOwner(owner).setLocation(self);
        unit.initialize(getGameState());
        dispatch(new UnitCreatedEvent(unit));
        return unit;
    }

    public void play(Animation animation) {
        dispatch(new AnimationEvent((Node) this, animation));
    }

    @Override
    public void initialize(GameState gameState) {
        super.initialize(gameState);
        if (unit != null) {
            unit.setLocation((Node) this);
            unit.initialize(gameState);
        }
    }

}
