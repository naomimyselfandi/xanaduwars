package io.github.naomimyselfandi.xanaduwars.core.filter;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.util.function.Predicate;

/// A filter for pairs of objects. These are typically used to restrict action
/// targets and the scope of rules.
@JsonDeserialize(using = FilterDeserializer.class)
public interface Filter<T> extends Predicate<T> {

    /// Check if an object passes this filter.
    @Override
    boolean test(T input);

}
