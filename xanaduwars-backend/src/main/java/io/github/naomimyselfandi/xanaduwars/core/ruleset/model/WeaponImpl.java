package io.github.naomimyselfandi.xanaduwars.core.ruleset.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.naomimyselfandi.xanaduwars.core.scripting.Script;
import io.github.naomimyselfandi.xanaduwars.ext.JsonCommentable;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;
import java.util.Map;

@Data
@JsonCommentable
class WeaponImpl implements Weapon {

    @JsonProperty
    private @NotNull @Valid Name name;

    @JsonProperty
    private @Nullable IFF alignment = IFF.FOE;

    @JsonProperty
    private @NotNull @Valid Script precondition = Script.NULL;

    @JsonProperty
    private @NotNull @Valid Script validation = Script.NULL;

    @JsonProperty
    private @NotNull @Valid Script effect = Script.NULL;

    @JsonProperty
    private @NotNull @Valid Script supplyCost = Script.NULL;

    @JsonProperty
    private @NotNull @Valid Script aetherCost = Script.NULL;

    @JsonProperty
    private @PositiveOrZero int minimumRange = 0;

    @JsonProperty
    private @PositiveOrZero int maximumRange = 1;

    @JsonProperty
    private @NotEmpty Map<@NotNull WeaponTarget, @NotNull @Positive Integer> damage = Map.of();

    @Override
    @JsonProperty
    public @Unmodifiable List<TargetSpec> targets() {
        if (damage.keySet().stream().anyMatch(StructureTag.class::isInstance)) {
            return List.of(new TargetSpec.AssetTargetSpec(IFF.FOE, minimumRange, maximumRange));
        } else {
            return List.of(new TargetSpec.UnitTargetSpec(IFF.FOE, minimumRange, maximumRange));
        }
    }

    @Override
    public String toString() {
        return name.name();
    }

}
