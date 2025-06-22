package io.github.naomimyselfandi.xanaduwars.core.ruleset.service;

import io.github.naomimyselfandi.xanaduwars.core.ruleset.Ruleset;

/// A factory that loads rulesets.
public interface RulesetService {

    /// Load a specific ruleset by its version number.
    /// @implSpec Rulesets are cached for performance.
    Ruleset load(Version version);

}
