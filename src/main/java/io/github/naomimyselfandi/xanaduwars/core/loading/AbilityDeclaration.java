package io.github.naomimyselfandi.xanaduwars.core.loading;

import com.fasterxml.jackson.databind.JsonNode;
import io.github.naomimyselfandi.xanaduwars.core.messages.PreflightQuery;
import io.github.naomimyselfandi.xanaduwars.core.model.*;
import io.github.naomimyselfandi.xanaduwars.core.script.Script;
import io.github.naomimyselfandi.xanaduwars.util.JsonImmutableList;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

@Getter(AccessLevel.PACKAGE)
@Setter(AccessLevel.PACKAGE)
class AbilityDeclaration extends AbstractSpecification implements Ability {

    record Proposal(JsonNode target, Cost cost) {}

    @JsonImmutableList
    @Getter(onMethod_ = @Override)
    private @NotNull List<AbilityTag> tags = List.of();

    private @NotNull @Valid Target<?> target = TargetOfNothing.NOTHING;
    private @NotNull @Valid Script supplyCost = Script.ZERO;
    private @NotNull @Valid Script aetherCost = Script.ZERO;
    private @NotNull @Valid Script focusCost = Script.ZERO;
    private @NotNull @Valid Script filter = Script.TRUE;
    private @NotNull @Valid Script effect;

    @Override
    public Object unpack(Actor actor, JsonNode target) throws CommandException {
        if (this.target.unpack(actor, target) instanceof Object object) {
            return object;
        } else {
            throw new CommandException("Malformed target '%s' for '%s'.".formatted(target, getName()));
        }
    }

    @Override
    public void validate(Actor actor, Object target) throws CommandException {
        if (!actor.getGameState().evaluate(new PreflightQuery(actor, this))) {
            throw new CommandException("Can't use '%s' right now.".formatted(getName()));
        } else if (!this.target.validate(actor, target)) {
            throw new CommandException("Invalid target for '%s' (in target specification).".formatted(getName()));
        } else if (!executeFilter(actor, target)) {
            throw new CommandException("Invalid target for '%s' (in scripted validation).".formatted(getName()));
        }
        var cost = getCost(actor, target);
        var player = actor.asPlayer();
        if (cost.supplyCost() > player.getSupplies()) {
            throw new CommandException("Insufficient supplies.");
        } else if (cost.aetherCost() > player.getAether()) {
            throw new CommandException("Insufficient aether.");
        } else if (cost.focusCost() > player.getFocus()) {
            throw new CommandException("Insufficient focus.");
        }
    }

    @Override
    public Cost getCost(Actor actor, Object target) {
        var gameState = actor.getGameState();
        var arguments = makeArgumentMap(actor, target);
        return new Cost(
                supplyCost.executeNotNull(gameState, arguments),
                aetherCost.executeNotNull(gameState, arguments),
                focusCost.executeNotNull(gameState, arguments)
        );
    }

    @Override
    public boolean execute(Actor actor, Object target) {
        var player = actor.asPlayer();
        var cost = getCost(actor, target);
        player.setSupplies(player.getSupplies() - cost.supplyCost());
        player.setAether(player.getAether() - cost.aetherCost());
        player.setFocus(player.getFocus() - cost.focusCost());
        actor.setActiveAbilities(augment(actor.getActiveAbilities()));
        if (actor.equals(player)) {
            player.setUsedAbilities(augment(player.getUsedAbilities()));
        }
        var gameState = actor.getGameState();
        var arguments = makeArgumentMap(actor, target);
        return !Boolean.FALSE.equals(effect.execute(gameState, arguments));
    }

    @Override
    public Stream<Object> propose(Actor actor) {
        return target
                .propose(actor)
                .filter(it -> executeFilter(actor, it))
                .map(it -> new Proposal(target.pack(it), getCost(actor, it)));
    }

    Map<String, Object> makeArgumentMap(Actor actor, Object target) {
        return Map.of("actor", actor, "target", target);
    }

    private boolean executeFilter(Actor actor, Object target) {
        return filter.executeNotNull(actor.getGameState(), makeArgumentMap(actor, target));
    }

    private List<Ability> augment(List<Ability> abilities) {
        return Stream.concat(abilities.stream(), Stream.of(this)).toList();
    }

}
