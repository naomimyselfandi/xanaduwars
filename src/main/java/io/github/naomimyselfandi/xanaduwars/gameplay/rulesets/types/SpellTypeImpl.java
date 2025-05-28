package io.github.naomimyselfandi.xanaduwars.gameplay.rulesets.types;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.github.naomimyselfandi.xanaduwars.gameplay.*;
import io.github.naomimyselfandi.xanaduwars.gameplay.common.*;
import io.github.naomimyselfandi.xanaduwars.gameplay.query.Rule;
import io.github.naomimyselfandi.xanaduwars.gameplay.rulesets.actions.SimpleActionMixin;
import io.github.naomimyselfandi.xanaduwars.gameplay.rulesets.rules.SubjectRule;
import io.github.naomimyselfandi.xanaduwars.gameplay.rulesets.rules.TargetRule;
import io.github.naomimyselfandi.xanaduwars.ext.JsonComment;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;

import java.util.List;

/// The standard spell implementation.
@JsonComment
@Getter(onMethod_ = @JsonGetter)
@Setter(onMethod_ = @JsonSetter, value = AccessLevel.PACKAGE)
@JsonTypeInfo(use = JsonTypeInfo.Id.MINIMAL_CLASS, property = "spellType")
public abstract class SpellTypeImpl<T> implements SpellType<T>, SimpleActionMixin<Player, T> {

    /// Initialize the names and indexes of a list of standard spell types.
    /// @throws IndexOutOfBoundsException if the lists have different sizes.
    public static void initialize(List<? extends SpellTypeImpl<?>> spellTypes, List<String> names) {
        var length = Math.max(spellTypes.size(), names.size()); // Ensures we throw on a mismatch.
        for (var i = 0; i < length; i++) {
            spellTypes.get(i).id(new SpellTypeId(i)).name(new Name(names.get(i)));
        }
    }

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private @NotNull SpellTypeId id;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private @NotNull Name name;

    private @NotNull TagSet tags = TagSet.EMPTY;

    @JsonDeserialize(contentAs = SubjectRule.class)
    private @NotNull List<@NotNull @Valid Rule<?, ?>> subjectRules = List.of();

    @JsonDeserialize(contentAs = TargetRule.class)
    private @NotNull List<@NotNull @Valid Rule<?, ?>> targetRules = List.of();

    private @NotNull @PositiveOrZero Integer focusCost;

    @Override
    public int cost(Resource resource, Player user, T target) {
        return resource == Resource.FOCUS ? focusCost : 0;
    }

    @Override
    public Execution execute(Player user, T target) {
        user.addActiveSpell(this);
        onCast(user, target);
        return Execution.SUCCESSFUL;
    }

    abstract void onCast(Player user, T target);

    @Override
    public String toString() {
        return "%s(%s)".formatted(id, name);
    }

}
