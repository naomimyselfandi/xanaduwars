package io.github.naomimyselfandi.xanaduwars.util;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.experimental.Delegate;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Set;

@SuppressWarnings("EqualsDoesntCheckParameterClass")
record ImmutableSet<T>(@Unmodifiable @Delegate @JsonValue Set<T> source) implements Set<T> {

    /// An immutable set.
    public ImmutableSet(Set<T> source) {
        this.source = Set.copyOf(source);
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
