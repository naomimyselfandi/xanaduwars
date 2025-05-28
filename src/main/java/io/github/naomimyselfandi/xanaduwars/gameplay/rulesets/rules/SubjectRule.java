package io.github.naomimyselfandi.xanaduwars.gameplay.rulesets.rules;

import io.github.naomimyselfandi.xanaduwars.gameplay.query.Validation;
import io.github.naomimyselfandi.xanaduwars.gameplay.rulesets.filter.Filter;
import io.github.naomimyselfandi.xanaduwars.gameplay.queries.SubjectQuery;
import io.github.naomimyselfandi.xanaduwars.ext.ConvenienceMixin;

/// A common game rule that can be filtered by subject.
public interface SubjectRule<Q extends SubjectQuery<V, S>, V, S> extends GameRule<Q, V> {

    /// A filter that scopes this rule by its subject.
    Filter<S> subjectFilter();

    @Override
    default boolean handles(Q query, V value) {
        return subjectFilter().test(query.subject());
    }

    /// A subject rule that acts as a validator.
    @ConvenienceMixin
    interface Validator<Q extends SubjectQuery<Boolean, S> & Validation, S> extends SubjectRule<Q, Boolean, S>,
            GameRule.Validator<Q> {

        @Override
        default boolean handles(Q query, Boolean value) {
            return SubjectRule.super.handles(query, value) && GameRule.Validator.super.handles(query, value);
        }

    }

}
