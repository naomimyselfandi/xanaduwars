package io.github.naomimyselfandi.xanaduwars.gameplay.rulesets.filter;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.util.function.BiPredicate;

/// A filter for pairs of objects. These are typically used to restrict action
/// targets and the scope of rules.
@JsonDeserialize(using = BiFilterDeserializer.class)
public interface BiFilter<S, T> extends BiPredicate<S, T> {

    /// Check if a pair of objects pass this filter. By analogy to actions, the
    /// first object is called the subject and the second is called the target.
    boolean test(S subject, T target);

}
