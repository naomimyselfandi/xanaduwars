package io.github.naomimyselfandi.xanaduwars.gameplay.rulesets.types;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.github.naomimyselfandi.xanaduwars.gameplay.common.Resource;
import io.github.naomimyselfandi.xanaduwars.gameplay.query.Rule;
import io.github.naomimyselfandi.xanaduwars.gameplay.TileType;
import io.github.naomimyselfandi.xanaduwars.gameplay.UnitType;
import io.github.naomimyselfandi.xanaduwars.gameplay.rulesets.rules.SubjectRule;
import io.github.naomimyselfandi.xanaduwars.gameplay.rulesets.rules.TargetRule;
import io.github.naomimyselfandi.xanaduwars.gameplay.common.*;
import io.github.naomimyselfandi.xanaduwars.ext.JsonComment;
import jakarta.validation.Valid;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/// The standard tile type implementation.
@JsonComment
@RequiredArgsConstructor
@Getter(onMethod_ = @JsonGetter)
@Setter(onMethod_ = @JsonSetter, value = AccessLevel.PACKAGE)
public final class TileTypeImpl implements TileType {

    private final @NotNull @Valid TileTypeId id;

    private final @NotNull Name name;

    private @NotNull TagSet tags = TagSet.EMPTY;

    @JsonDeserialize(contentAs = SubjectRule.class)
    private @NotNull List<@NotNull @Valid Rule<?, ?>> subjectRules = List.of();

    @JsonDeserialize(contentAs = TargetRule.class)
    private @NotNull List<@NotNull @Valid Rule<?, ?>> targetRules = List.of();

    private @NotNull Map<@NotNull Tag, @Positive Double> movementTable = Map.of();

    private @NotNull Percent cover = Percent.FULL;

    private @Nullable TileType foundation;

    private @NotNull Map<@NotNull Resource, @NotNull @Positive Integer> costs = Map.of();

    private @PositiveOrZero int buildTime;

    private @NotNull Map<@NotNull Resource, @NotNull @Positive Integer> income = Map.of();

    private @NotNull Set<@NotNull UnitType> deploymentRoster = Set.of();

    @Override
    public Optional<TileType> foundation() {
        return Optional.ofNullable(foundation);
    }

    @AssertTrue
    @JsonIgnore
    boolean isConsistent() {
        return (foundation == null) == (buildTime == 0);
    }

    @Override
    public String toString() {
        return "%s(%s)".formatted(id, name);
    }

}
