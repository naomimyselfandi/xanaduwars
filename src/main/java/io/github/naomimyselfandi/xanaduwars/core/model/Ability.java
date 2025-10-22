package io.github.naomimyselfandi.xanaduwars.core.model;

import com.fasterxml.jackson.databind.JsonNode;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;
import java.util.stream.Stream;

/// An in-game ability. Both units and players have abilities; the latter
/// typically represent their spells.
public interface Ability extends Specification {

    /// The costs to use an ability.
    record Cost(int supplyCost, int aetherCost, int focusCost) {}

    /// Get this ability's unique name.
    @Override String getName();

    /// Get any tags that apply to this ability.
    @Unmodifiable List<AbilityTag> getTags();

    /// Unpack the JSON form of a target object. The target is not necessarily
    /// valid, although this will never return a unit which the actor's player
    /// cannot perceive.
    /// @apiNote Passing around raw JSON objects like this is very flexible, but
    /// is a very weak abstraction. This may be replaced with a proper DTO in
    /// the future.
    /// @throws CommandException if the JSON target is syntactically or
    /// semantically invalid.
    Object unpack(Actor actor, JsonNode target) throws CommandException;

    /// Validate if an actor can use this ability on a target. This checks both
    /// the validity of the target itself and whether the actor's player can pay
    /// any associated costs, but does not check whether the actor is currently
    /// able to take actions at all.
    /// @throws CommandException if the actor cannot use this ability on the
    /// target, or the actor's player cannot afford it.
    void validate(Actor actor, Object target) throws CommandException;

    /// Get the cost for an actor to use this ability on a target.
    Cost getCost(Actor actor, Object target);

    /// Instruct an actor to use this ability on a target. This assumes the
    /// input is valid and automatically pays costs. The return value indicates
    /// whether the ability was used fully, or was interrupted by some hidden
    /// information. (Interruptions are an ordinary part of gameplay and do not
    /// indicate a game rule violation.)
    boolean execute(Actor actor, Object target);

    /// Propose targets for this ability. These are typically in the same format
    /// as [unpack][#unpack(Actor, JsonNode)] expects, but may be in any format
    /// which the frontend can display. In particular, if this ability targets a
    /// path, enumerating all valid targets may yield an impractically large
    /// result, in which case this may simply return valid destination tiles.
    Stream<Object> propose(Actor actor);

}
