package io.github.naomimyselfandi.xanaduwars.gameplay.rulesets.rules;

import io.github.naomimyselfandi.xanaduwars.gameplay.query.Validation;
import io.github.naomimyselfandi.xanaduwars.gameplay.rulesets.filter.BiFilter;
import io.github.naomimyselfandi.xanaduwars.gameplay.queries.TargetQuery;
import io.github.naomimyselfandi.xanaduwars.ext.ConvenienceMixin;

/// A common game rule that can be filtered by subject or target.
public interface TargetRule<Q extends TargetQuery<V, S, T>, V, S, T> extends SubjectRule<Q, V, S> {

    /// A filter that scopes this rule by its subject and target.
    BiFilter<S, T> targetFilter();

    @Override
    default boolean handles(Q query, V value) {
        return SubjectRule.super.handles(query, value) && targetFilter().test(query.subject(), query.target());
    }

    /// A subject rule that acts as a validator.
    @ConvenienceMixin
    interface Validator<Q extends TargetQuery<Boolean, S, T> & Validation, S, T> extends TargetRule<Q, Boolean, S, T>,
            GameRule.Validator<Q> {

        @Override
        default boolean handles(Q query, Boolean value) {
            return TargetRule.super.handles(query, value) && GameRule.Validator.super.handles(query, value);
        }

    }

}
