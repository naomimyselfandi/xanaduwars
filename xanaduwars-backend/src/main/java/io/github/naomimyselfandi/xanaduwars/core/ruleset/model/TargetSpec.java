package io.github.naomimyselfandi.xanaduwars.core.ruleset.model;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.github.naomimyselfandi.xanaduwars.ext.ExcludeFromCoverageReport;
import jakarta.validation.constraints.PositiveOrZero;
import org.jetbrains.annotations.Nullable;

/// A description of one of an ability's targets.
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = TargetSpec.AssetTargetSpec.class, name = "Asset"),
        @JsonSubTypes.Type(value = TargetSpec.PathSpec.class, name = "Path"),
        @JsonSubTypes.Type(value = TargetSpec.StructureTargetSpec.class, name = "Structure"),
        @JsonSubTypes.Type(value = TargetSpec.StructureTypeTargetSpec.class, name = "StructureType"),
        @JsonSubTypes.Type(value = TargetSpec.TileTargetSpec.class, name = "Tile"),
        @JsonSubTypes.Type(value = TargetSpec.UnitTargetSpec.class, name = "Unit"),
        @JsonSubTypes.Type(value = TargetSpec.UnitTypeTargetSpec.class, name = "UnitType"),
})
public sealed interface TargetSpec {

    /// A description of an asset target. The asset must be visible and, if it
    /// is a unit, it must not be inside another unit.
    @ExcludeFromCoverageReport
    record AssetTargetSpec(@Nullable IFF iff, @PositiveOrZero int minimumRange, @PositiveOrZero int maximumRange)
            implements TargetSpec {}

    /// A description of a path target.
    @ExcludeFromCoverageReport
    record PathSpec() implements TargetSpec {}

    /// A description of a structure target. The structure must be visible.
    @ExcludeFromCoverageReport
    record StructureTargetSpec(@Nullable IFF iff, @PositiveOrZero int minimumRange, @PositiveOrZero int maximumRange)
            implements TargetSpec {}

    /// A description of a structure type target.
    @ExcludeFromCoverageReport
    record StructureTypeTargetSpec() implements TargetSpec {}

    /// A description of a tile target.
    @ExcludeFromCoverageReport
    record TileTargetSpec(boolean visible, @PositiveOrZero int minimumRange, @PositiveOrZero int maximumRange)
            implements TargetSpec {}

    /// A description of a unit target. The unit must be visible and not inside
    /// another unit.
    @ExcludeFromCoverageReport
    record UnitTargetSpec(@Nullable IFF iff, @PositiveOrZero int minimumRange, @PositiveOrZero int maximumRange)
            implements TargetSpec {}

    /// A description of a unit type target.
    @ExcludeFromCoverageReport
    record UnitTypeTargetSpec() implements TargetSpec {}

}
