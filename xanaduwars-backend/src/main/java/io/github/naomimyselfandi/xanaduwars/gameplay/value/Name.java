package io.github.naomimyselfandi.xanaduwars.gameplay.value;

import com.fasterxml.jackson.annotation.JsonValue;
import jakarta.validation.constraints.NotNull;

import java.util.function.Predicate;
import java.util.regex.Pattern;

/// The name of a type or action. Valid names begin with an uppercase letter and
/// contain only alphanumeric characters.
/// @param text The string form of this name.
public record Name(@NotNull @JsonValue String text) implements Comparable<Name> {

    /// A predicate that matches valid names.
    public static final Predicate<String> VALID = Pattern.compile("[A-Z][A-Za-z0-9]*").asMatchPredicate();

    /// The name of a type or action. Valid names begin with an uppercase letter and
    /// contain only alphanumeric characters.
    /// @param text The string form of this name.
    /// @throws IllegalArgumentException if the string form is invalid.
    public Name {
        if (!VALID.test(text)) {
            throw new IllegalArgumentException("Malformed name '%s'.".formatted(text));
        }
    }

    @Override
    public int compareTo(Name that) {
        return this.text.compareTo(that.text);
    }

    @Override
    public String toString() {
        return text;
    }

}
