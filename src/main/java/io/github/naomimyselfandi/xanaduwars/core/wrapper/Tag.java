package io.github.naomimyselfandi.xanaduwars.core.wrapper;

import com.fasterxml.jackson.annotation.JsonValue;

import java.util.function.Predicate;
import java.util.regex.Pattern;

/// A tag used to categorize related objects. A tag is a simple textual label.
/// Valid tags begin with an uppercase letter and contain only alphanumeric
/// characters.
/// @param text The string form of this tag.
public record Tag(@JsonValue String text) implements StringWrapper, Comparable<Tag> {

    /// A predicate that matches valid tags.
    public static final Predicate<String> VALID = Pattern.compile("[A-Z][A-Za-z0-9]*").asMatchPredicate();

    /// A tag used to categorize related objects. A tag is a simple textual label.
    /// Valid tags begin with an uppercase letter and contain only alphanumeric
    /// characters.
    /// @param text The string form of this tag.
    /// @throws IllegalArgumentException if the string form is invalid.
    public Tag {
        if (!VALID.test(text)) {
            throw new IllegalArgumentException("Malformed tag '%s'.".formatted(text));
        }
    }

    @Override
    public int compareTo(Tag that) {
        return this.text.compareTo(that.text);
    }

    @Override
    public String toString() {
        return text;
    }

}
