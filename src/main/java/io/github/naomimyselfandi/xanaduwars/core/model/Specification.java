package io.github.naomimyselfandi.xanaduwars.core.model;

import io.github.naomimyselfandi.xanaduwars.core.message.ContextualRuleSource;
import io.github.naomimyselfandi.xanaduwars.core.script.ScriptConstant;

/// A specification for some type of in-game object. Unit types, tile types,
/// playable commanders, and abilities are all specifications.
public interface Specification extends ContextualRuleSource, ScriptConstant {

    /// Get this specification's unique name.
    String getName();

}
