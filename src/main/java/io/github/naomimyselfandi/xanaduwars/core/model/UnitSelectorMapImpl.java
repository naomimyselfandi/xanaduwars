package io.github.naomimyselfandi.xanaduwars.core.model;

import lombok.experimental.Delegate;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;
import java.util.Map;
import java.util.function.BiPredicate;
import java.util.function.Predicate;
import java.util.stream.Stream;

record UnitSelectorMapImpl<V extends Comparable<V>>(@Delegate Map<UnitSelector, V> map) implements UnitSelectorMap<V> {

    UnitSelectorMapImpl(Map<UnitSelector, V> map) {
        this.map = Map.copyOf(map);
    }

    static final UnitSelectorMapImpl<?> EMPTY = new UnitSelectorMapImpl<String>(Map.of());

    @Override
    public @Nullable V min(Unit unit) {
        return values(unit, UnitSelector::test).min(Comparator.naturalOrder()).orElse(null);
    }

    @Override
    public @Nullable V min(UnitType unitType) {
        return values(unitType, UnitSelector::test).min(Comparator.naturalOrder()).orElse(null);
    }

    @Override
    public @Nullable V max(Unit unit) {
        return values(unit, UnitSelector::test).max(Comparator.naturalOrder()).orElse(null);
    }

    @Override
    public @Nullable V max(UnitType unitType) {
        return values(unitType, UnitSelector::test).max(Comparator.naturalOrder()).orElse(null);
    }

    @Override
    @SuppressWarnings("EqualsDoesntCheckParameterClass")
    public boolean equals(@Nullable Object obj) {
        return map.equals(obj);
    }

    @Override
    public int hashCode() {
        return map.hashCode();
    }

    @Override
    public String toString() {
        return map.toString();
    }

    private <T> Predicate<Entry<UnitSelector, V>> matches(T key, BiPredicate<UnitSelector, T> callback) {
        return entry -> callback.test(entry.getKey(), key);
    }

    private <T> Stream<V> values(T key, BiPredicate<UnitSelector, T> callback) {
        return entrySet().stream().filter(matches(key, callback)).map(Map.Entry::getValue);
    }

}
