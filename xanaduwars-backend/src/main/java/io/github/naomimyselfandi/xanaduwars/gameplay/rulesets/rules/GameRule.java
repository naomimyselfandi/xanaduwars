package io.github.naomimyselfandi.xanaduwars.gameplay.rulesets.rules;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.github.naomimyselfandi.xanaduwars.gameplay.query.Query;
import io.github.naomimyselfandi.xanaduwars.gameplay.query.Rule;
import io.github.naomimyselfandi.xanaduwars.gameplay.query.Validation;
import io.github.naomimyselfandi.xanaduwars.ext.ConvenienceMixin;
import io.github.naomimyselfandi.xanaduwars.ext.DeserializationHelper;
import io.github.naomimyselfandi.xanaduwars.ext.JsonCommentable;

/// A common game rule.
@JsonCommentable
@DeserializationHelper
@JsonTypeInfo(use = JsonTypeInfo.Id.MINIMAL_CLASS, property = "rule")
public interface GameRule<Q extends Query<V>, V> extends Rule<Q, V> {

    /// A common game rule that acts as a validator.
    @ConvenienceMixin
    interface Validator<Q extends Validation> extends GameRule<Q, Boolean>,
            io.github.naomimyselfandi.xanaduwars.gameplay.query.Validator<Q> {}

}
