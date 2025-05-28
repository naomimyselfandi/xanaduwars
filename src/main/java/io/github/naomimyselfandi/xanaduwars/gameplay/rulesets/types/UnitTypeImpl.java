package io.github.naomimyselfandi.xanaduwars.gameplay.rulesets.types;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.github.naomimyselfandi.xanaduwars.gameplay.*;
import io.github.naomimyselfandi.xanaduwars.gameplay.query.Rule;
import io.github.naomimyselfandi.xanaduwars.gameplay.rulesets.actions.ability.Ability;
import io.github.naomimyselfandi.xanaduwars.gameplay.rulesets.rules.SubjectRule;
import io.github.naomimyselfandi.xanaduwars.gameplay.rulesets.rules.TargetRule;
import io.github.naomimyselfandi.xanaduwars.gameplay.common.*;
import io.github.naomimyselfandi.xanaduwars.ext.JsonComment;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.util.List;
import java.util.Map;

/// The standard unit type implementation.
@JsonComment
@RequiredArgsConstructor
@Getter(onMethod_ = @JsonGetter)
@Setter(onMethod_ = @JsonSetter, value = AccessLevel.PACKAGE)
public final class UnitTypeImpl implements UnitType {

    private final @NotNull @Valid UnitTypeId id;

    private final @NotNull Name name;

    private @NotNull TagSet tags = TagSet.EMPTY;

    @JsonDeserialize(contentAs = SubjectRule.class)
    private @NotNull List<@NotNull @Valid Rule<?, ?>> subjectRules = List.of();

    @JsonDeserialize(contentAs = TargetRule.class)
    private @NotNull List<@NotNull @Valid Rule<?, ?>> targetRules = List.of();

    private @NotNull Map<@NotNull Resource, @Positive @NotNull Integer> costs = Map.of();

    private @Positive int vision, speed;

    private @NotNull @Valid Range range = Range.MELEE;

    private @NotNull Map<@NotNull NodeType, @NotNull Scalar> damageTable = Map.of();

    private @NotNull TagSet hangar = TagSet.EMPTY;

    @JsonDeserialize(contentAs = Ability.class)
    private @NotNull List<@NotNull @Valid Action<Unit, ?>> abilities = List.of();

    @Override
    public String toString() {
        return "%s(%s)".formatted(id, name);
    }

}
