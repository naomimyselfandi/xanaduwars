package io.github.naomimyselfandi.xanaduwars.core.impl;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.github.naomimyselfandi.xanaduwars.core.message.ContextualRuleSource;
import io.github.naomimyselfandi.xanaduwars.core.messages.DefeatedEvent;
import io.github.naomimyselfandi.xanaduwars.core.messages.ReadyStateQuery;
import io.github.naomimyselfandi.xanaduwars.core.model.*;
import io.github.naomimyselfandi.xanaduwars.util.JsonImmutableList;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldNameConstants;

import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

@NoArgsConstructor
@Getter(onMethod_ = @Override)
@FieldNameConstants(asEnum = true)
final class PlayerImpl extends AbstractElement implements Player {

    @JsonIgnore
    @Setter(AccessLevel.PACKAGE)
    private int position = -1;

    private int team, supplies, focus, aether;

    @JsonSerialize(using = NameSerializer.class)
    private Commander commander;

    @JsonImmutableList
    @JsonSerialize(contentUsing = NameSerializer.class)
    private @NotNull List<Ability> abilities = List.of();

    @JsonImmutableList
    @JsonSerialize(contentUsing = NameSerializer.class)
    private @NotNull List<Ability> activeAbilities = List.of();

    @JsonImmutableList
    @JsonSerialize(contentUsing = NameSerializer.class)
    private @NotNull List<Ability> usedAbilities = List.of();

    private boolean defeated;

    @Override
    public PlayerImpl setTeam(int team) {
        if (team != this.team) {
            this.team = team;
            clearQueryCache();
        }
        return this;
    }

    @Override
    public PlayerImpl setCommander(Commander commander) {
        if (!commander.equals(this.commander)) {
            this.commander = Objects.requireNonNull(commander);
            clearQueryCache();
        }
        return this;
    }

    @Override
    public Player asPlayer() {
        return this;
    }

    @Override
    @JsonIgnore
    public boolean isReady() {
        return getGameState().evaluate(new ReadyStateQuery(this));
    }

    @Override
    public PlayerImpl setAbilities(List<Ability> abilities) {
        if (!abilities.equals(this.abilities)) {
            this.abilities = List.copyOf(abilities);
            clearQueryCache();
        }
        return this;
    }

    @Override
    public Actor setActiveAbilities(List<Ability> abilities) {
        if (!abilities.equals(this.activeAbilities)) {
            this.activeAbilities = List.copyOf(abilities);
            clearQueryCache();
        }
        return this;
    }

    @Override
    public Player setUsedAbilities(List<Ability> abilities) {
        if (!abilities.equals(this.usedAbilities)) {
            this.usedAbilities = List.copyOf(abilities);
            clearQueryCache();
        }
        return this;
    }

    @Override
    public Player setDefeated(boolean defeated) {
        if (defeated != this.defeated) {
            this.defeated = defeated;
            if (defeated) {
                dispatch(new DefeatedEvent(this));
            } else {
                clearQueryCache();
            }
        }
        return this;
    }

    @Override
    public Player setSupplies(int supplies) {
        if (supplies != this.supplies) {
            this.supplies = supplies;
            clearQueryCache();
        }
        return this;
    }

    @Override
    public Player setAether(int aether) {
        if (aether != this.aether) {
            this.aether = aether;
            clearQueryCache();
        }
        return this;
    }

    @Override
    public Player setFocus(int focus) {
        if (focus != this.focus) {
            this.focus = focus;
            clearQueryCache();
        }
        return this;
    }

    @Override
    @JsonIgnore
    public Stream<Unit> getUnits() {
        return getGameState().getUnits().filter(it -> equals(it.getOwner()));
    }

    @Override
    public boolean perceives(Node node) {
        return isAlly(node) || getUnits().anyMatch(it -> it.perceives(node));
    }

    @Override
    public boolean isAlly(Element other) {
        return switch (other) {
            case Player that -> team == that.getTeam();
            case Tile _ -> false;
            case Unit unit -> isAlly(unit.getOwner());
        };
    }

    @Override
    public boolean isEnemy(Element other) {
        return switch (other) {
            case Player that -> team != that.getTeam();
            case Tile _ -> false;
            case Unit unit -> isEnemy(unit.getOwner());
        };
    }

    @Override
    Stream<ContextualRuleSource> getAssociatedObjects() {
        return Stream.concat(Stream.of(commander), activeAbilities.stream());
    }

    @Override
    public String toString() {
        return "%s(%d)".formatted(commander.getName(), position);
    }

}
