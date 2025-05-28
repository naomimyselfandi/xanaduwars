package io.github.naomimyselfandi.xanaduwars.gameplay.common;

import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.experimental.Delegate;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/// An immutable set of tags.
@JsonDeserialize(using = TagSetDeserializer.class)
@SuppressWarnings("EqualsDoesntCheckParameterClass")
public record TagSet(@Unmodifiable @Delegate Set<Tag> tags) implements @Unmodifiable Set<Tag>, Predicate<Tag> {

    /// A collector that produces tag sets.
    public static final Collector<Tag, ?, TagSet> COLLECTOR = Collectors
            .collectingAndThen(Collectors.toSet(), TagSet::new);

    /// The empty tag set.
    public static final TagSet EMPTY = new TagSet(Set.of());

    /// Construct a tag set containing the given tags.
    public static TagSet of(Tag... tags) {
        return new TagSet(Set.of(tags));
    }

    /// An immutable set of tags.
    public TagSet(Set<Tag> tags) {
        this.tags = Set.copyOf(tags);
    }

    /// Check if this tag set contains a tag.
    @Override
    public boolean test(Tag tag) {
        return tags.contains(tag);
    }

    /// Take the union of this tag set and some other tags.
    public TagSet union(Collection<Tag> that) {
        return Stream.concat(tags.stream(), that.stream()).collect(COLLECTOR);
    }

    /// Take the intersection of this tag set and some other tags.
    public TagSet intersection(Collection<Tag> that) {
        return that.stream().filter(this).collect(COLLECTOR);
    }

    /// Take the union of this tag set and some other tags.
    public TagSet with(Tag... tags) {
        return union(Arrays.asList(tags));
    }

    @JsonValue
    public List<Tag> toList() {
        return stream().toList();
    }

    @Override
    public Stream<Tag> stream() {
        return tags.stream().sorted();
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        return tags.equals(obj);
    }

    @Override
    public int hashCode() {
        return tags.hashCode();
    }

    @Override
    public String toString() {
        return stream().map(Tag::text).collect(Collectors.joining(", ", "[", "]"));
    }

}
