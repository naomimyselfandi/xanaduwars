package io.github.naomimyselfandi.xanaduwars.core.common;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.With;
import lombok.experimental.Accessors;

import java.util.HashMap;
import java.util.Map;

import static io.github.naomimyselfandi.xanaduwars.core.common.Iff.*;
import static io.github.naomimyselfandi.xanaduwars.core.common.Kind.*;

/// A specification of one of an action's targets.
///
/// @param filters The main target filters.
/// @param minRange The minimum targeted range. This is applicable only if the
/// actor and target are physical (i.e. neither is a player).
/// @param maxRange The maximum targeted range. This is applicable only if the
/// actor and target are physical (i.e. neither is a player).
/// @param path Whether this describes the special "target" used by movement
/// actions.
@JsonDeserialize(builder = TargetSpec.TargetSpecBuilder.class)
public record TargetSpec(
        @JsonAnyGetter
        Map<TargetFilter, Boolean> filters,
        @With @PositiveOrZero int minRange,
        @With@PositiveOrZero int maxRange,
        boolean path
) {

    // For validation.
    private static final TargetSpec PATH = builder().path(true).build();

    /// A specification of one of an action's targets.
    /// @see TargetSpec The class-level documentation has detailed information
    /// on how this specification should be interpreted.
    public TargetSpec(Map<TargetFilter, Boolean> filters, int minRange, int maxRange, boolean path) {
        this.filters = Map.copyOf(filters);
        this.minRange = minRange;
        this.maxRange = maxRange;
        this.path = path;
    }

    @AssertTrue
    boolean isValid() {
        return path ? equals(PATH) : (minRange <= maxRange);
    }

    @AssertTrue
    boolean hasIff() {
        return path || filters.keySet().stream().filter(filters::get).anyMatch(Iff.class::isInstance);
    }

    @AssertTrue
    boolean hasKind() {
        return path || filters.keySet().stream().filter(filters::get).anyMatch(Kind.class::isInstance);
    }

    /// Create a target spec builder.
    public static TargetSpecBuilder builder() {
        return new TargetSpecBuilder();
    }

    /// Create a target spec builder.
    @Data
    @JsonPOJOBuilder(withPrefix = "")
    @Accessors(fluent = true, chain = true)
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class TargetSpecBuilder {

        @JsonAnySetter
        private Map<TargetFilter, Boolean> filters = new HashMap<>();

        private int minRange;

        private int maxRange = 1;

        private boolean path;

        public TargetSpec build() {
            filters = new HashMap<>(filters);
            if (!filters.containsKey(NEUTRAL) && Boolean.TRUE.equals(filters.get(TILE))) {
                filters.put(NEUTRAL, true);
            }
            if (filters.containsKey(ALLY) && !filters.containsKey(OWN)) {
                filters.put(OWN, filters.get(ALLY));
            }
            return new TargetSpec(filters, minRange, maxRange, path);
        }

    }

}
