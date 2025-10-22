package io.github.naomimyselfandi.xanaduwars.util;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.experimental.Delegate;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;

@SuppressWarnings("EqualsDoesntCheckParameterClass")
record ImmutableList<T>(@Unmodifiable @Delegate @JsonValue List<T> source) implements List<T> {

    /// An immutable list.
    public ImmutableList(List<T> source) {
        this.source = List.copyOf(source);
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
