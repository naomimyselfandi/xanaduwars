package io.github.naomimyselfandi.xanaduwars.util;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.experimental.Delegate;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Map;

@SuppressWarnings("EqualsDoesntCheckParameterClass")
record ImmutableMap<K, V>(@Unmodifiable @Delegate @JsonValue Map<K, V> source) implements Map<K, V> {

    private static final ImmutableMap<?, ?> EMPTY = new ImmutableMap<>(Map.of());

    /// Construct an immutable map by copying another map.
    public ImmutableMap(Map<K, V> source) {
        this.source = Map.copyOf(source);
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        return source.equals(obj);
    }

    @Override
    public int hashCode() {
        return source.hashCode();
    }

    @Override
    public String toString() {
        return source.toString();
    }

}
