package io.github.naomimyselfandi.xanaduwars.core.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import io.github.naomimyselfandi.xanaduwars.util.NotCovered;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Map;

/// A map whose keys are unit selectors.
@NotCovered // Mockito coverage report bug workaround
public interface UnitSelectorMap<V extends Comparable<V>> extends @Unmodifiable Map<UnitSelector, V> {

    /// Get the minimum value matching some unit, if any.
    @Nullable V min(Unit unit);

    /// Get the minimum value matching some unit type, if any.
    @Nullable V min(UnitType unitType);

    /// Get the maximum value matching some unit, if any.
    @Nullable V max(Unit unit);

    /// Get the maximum value matching some unit type, if any.
    @Nullable V max(UnitType unitType);

    /// Get a unit selector map with no entries.
    static <V extends Comparable<V>> UnitSelectorMap<V> empty() {
        @SuppressWarnings("unchecked")
        var result = (UnitSelectorMap<V>) UnitSelectorMapImpl.EMPTY;
        return result;
    }

    /// Create a unit selector map by copying an ordinary map.
    @JsonCreator
    static <V extends Comparable<V>> UnitSelectorMap<V> copyOf(Map<UnitSelector, V> map) {
        return new UnitSelectorMapImpl<>(map);
    }

}
