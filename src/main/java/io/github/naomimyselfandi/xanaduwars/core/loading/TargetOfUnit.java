package io.github.naomimyselfandi.xanaduwars.core.loading;

import com.fasterxml.jackson.databind.JsonNode;
import io.github.naomimyselfandi.xanaduwars.core.model.Actor;
import io.github.naomimyselfandi.xanaduwars.core.model.CommandException;
import io.github.naomimyselfandi.xanaduwars.core.model.Tile;
import io.github.naomimyselfandi.xanaduwars.core.model.Unit;

import java.util.stream.Stream;

record TargetOfUnit(Target<Tile, Tile> base) implements Target<Unit, Unit> {

    @Override
    public Unit unpack(Actor actor, JsonNode target) throws CommandException {
        if (base.unpack(actor, target).getUnit() instanceof Unit unit && actor.asPlayer().perceives(unit)) {
            return unit;
        } else {
            // intentionally ambiguous message to avoid leaking hidden information
            throw new CommandException("Target unit does not exist or is hidden.");
        }
    }

    @Override
    public boolean validate(Actor actor, Object target) {
        var unit = (Unit) target;
        return (unit.getLocation() instanceof Tile tile)
                && base.validate(actor, tile)
                && actor.asPlayer().perceives(unit); // prevent time-of-check/time-of-use exploit
    }

    @Override
    public Stream<Unit> propose(Actor actor) {
        var player = actor.asPlayer();
        return base.propose(actor).map(Tile::getUnit).filter(it -> it != null && player.perceives(it));
    }

    @Override
    public JsonNode pack(Unit proposal) {
        // cast OK because we only propose units on tiles
        return base.pack((Tile) proposal.getLocation());
    }

}
