package io.github.naomimyselfandi.xanaduwars.gameplay.query;

import io.github.naomimyselfandi.xanaduwars.ext.ConvenienceMixin;

/// A rule that represents some sort of validation logic.
@ConvenienceMixin
public interface Validator<Q extends Validation> extends Rule<Q, Boolean> {

    /// Apply this validation logic.
    boolean isValid(Q query);

    @Override
    default boolean handles(Q query, Boolean value) {
        return !isValid(query);
    }

    @Override
    default Boolean handle(Q query, Boolean value) {
        return false;
    }

}
