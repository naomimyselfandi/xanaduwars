package io.github.naomimyselfandi.xanaduwars.core.impl;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.github.naomimyselfandi.xanaduwars.core.message.ContextualRuleSource;
import io.github.naomimyselfandi.xanaduwars.core.messages.*;
import io.github.naomimyselfandi.xanaduwars.core.model.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldNameConstants;

import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

@NoArgsConstructor
@Getter(onMethod_ = @Override)
@FieldNameConstants(asEnum = true)
final class UnitImpl extends AbstractNode implements Unit {

    private static final double HP_SCALE = 10_000;

    @JsonSerialize(using = NameSerializer.class)
    private @NotNull UnitType type;

    @JsonIgnore
    private Node location;

    @JsonSerialize(using = PositionSerializer.class)
    private Player owner;

    @Getter(AccessLevel.NONE)
    private int scaledHpPercent = (int) HP_SCALE;

    private boolean underConstruction;

    @JsonSerialize(contentUsing = NameSerializer.class)
    private List<Ability> activeAbilities = List.of();

    @Override
    @JsonIgnore
    public List<UnitTag> getTags() {
        return type.getTags();
    }

    @Override
    public UnitImpl setType(UnitType type) {
        if (!type.equals(this.type)) {
            this.type = type;
            dispatch(new GenericEvent(this));
        }
        return this;
    }

    @Override
    @JsonIgnore
    public Player asPlayer() {
        return getOwner();
    }

    @Override
    @JsonIgnore
    public boolean isReady() {
        return getGameState().evaluate(new ReadyStateQuery(this));
    }

    @Override
    public UnitImpl setOwner(Player owner) {
        if (!owner.equals(this.owner)) {
            this.owner = owner;
            dispatch(new GenericEvent(this));
        }
        return this;
    }

    @Override
    @JsonIgnore
    public UnitImpl setLocation(Node location) {
        if (!Objects.equals(location, this.location)) {
            if (this.location != null) {
                dispatch(new GenericEvent(this));
                this.location.setUnit(null);
            }
            this.location = location;
            location.setUnit(this);
            dispatch(new GenericEvent(this));
        }
        return this;
    }

    @Override
    @JsonIgnore
    public int getSpeed() {
        return evaluate(new GetSpeedQuery(this));
    }

    @Override
    @JsonIgnore
    public int getPerception() {
        return evaluate(new GetPerceptionQuery(this));
    }

    @Override
    public boolean perceives(Node node) {
        return evaluate(new IsNodePerceivedByUnitQuery(node, this));
    }

    @Override
    @JsonIgnore
    public int getMaxHp() {
        return evaluate(new GetMaxHpQuery(this));
    }

    @Override
    @JsonIgnore
    public boolean isAlive() {
        return scaledHpPercent > 0;
    }

    @Override
    @JsonIgnore
    public int getHp() {
        return Math.toIntExact(Math.round(scaledHpPercent * getMaxHp() / HP_SCALE));
    }

    @Override
    @JsonIgnore
    public Unit setHp(int hp) {
        return setHpPercent(hp / (double) getMaxHp());
    }

    @Override
    public double getHpPercent() {
        return scaledHpPercent / HP_SCALE;
    }

    @Override
    public UnitImpl setHpPercent(double hpPercent) {
        if (hpPercent < 0) {
            return setHpPercent(0);
        } else if (hpPercent > 1) {
            return setHpPercent(1);
        } else {
            var scaledHpPercent = Math.toIntExact(Math.round(hpPercent * HP_SCALE));
            if (scaledHpPercent != this.scaledHpPercent) {
                this.scaledHpPercent = scaledHpPercent;
                if (scaledHpPercent == 0) {
                    if (getUnit() instanceof Unit cargo) cargo.setHpPercent(0);
                    dispatch(new UnitDestroyedEvent(this));
                    if (location != null) location.setUnit(null);
                } else {
                    dispatch(new GenericEvent(this));
                }
            }
            return this;
        }
    }

    @Override
    public UnitImpl setUnderConstruction(boolean underConstruction) {
        if (underConstruction != this.underConstruction) {
            this.underConstruction = underConstruction;
            dispatch(new GenericEvent(this));
        }
        return this;
    }

    @Override
    @JsonIgnore
    public int getSupplyCost() {
        return type.getSupplyCost();
    }

    @Override
    @JsonIgnore
    public int getAetherCost() {
        return type.getAetherCost();
    }

    @Override
    @JsonIgnore
    public UnitSelector getHangar() {
        return type.getHangar();
    }

    @Override
    @JsonIgnore
    public List<Ability> getAbilities() {
        return evaluate(new UnitAbilityQuery(this));
    }

    @Override
    @JsonIgnore
    public List<Weapon> getWeapons() {
        return type.getWeapons();
    }

    @Override
    public Actor setActiveAbilities(List<Ability> abilities) {
        if (!abilities.equals(this.activeAbilities)) {
            this.activeAbilities = List.copyOf(abilities);
            dispatch(new GenericEvent(this));
        }
        return this;
    }

    @Override
    public double getDistance(Node other) {
        return location instanceof Tile ? location.getDistance(other) : Double.NaN;
    }

    @Override
    public boolean isAlly(Element other) {
        return getOwner().isAlly(other);
    }

    @Override
    public boolean isEnemy(Element other) {
        return getOwner().isEnemy(other);
    }

    @Override
    Stream<ContextualRuleSource> getAssociatedObjects() {
        var result = Stream.concat(Stream.of(type, getOwner()), activeAbilities.stream());
        if (location instanceof Tile) {
            result = Stream.concat(result, Stream.of(location));
        }
        return result;
    }

    @Override
    public String toString() {
        return "%s(%s)".formatted(type.getName(), location);
    }

}
