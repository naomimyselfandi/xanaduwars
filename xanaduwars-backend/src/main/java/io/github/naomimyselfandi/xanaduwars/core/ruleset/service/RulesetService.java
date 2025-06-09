package io.github.naomimyselfandi.xanaduwars.core.ruleset.service;

import io.github.naomimyselfandi.xanaduwars.core.ruleset.model.Ruleset;
import io.github.naomimyselfandi.xanaduwars.core.ruleset.model.Version;
import org.jetbrains.annotations.Nullable;

/// A factory that loads rulesets.
public interface RulesetService {

    /// Load a specific ruleset by its version number.
    /// @implSpec Rulesets are cached for performance.
    @Nullable Ruleset load(Version version);

}
