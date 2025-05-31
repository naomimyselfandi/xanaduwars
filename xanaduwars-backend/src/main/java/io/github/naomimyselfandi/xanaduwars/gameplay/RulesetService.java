package io.github.naomimyselfandi.xanaduwars.gameplay;

import io.github.naomimyselfandi.xanaduwars.gameplay.value.Version;

import java.util.Optional;

/// A factory that loads rulesets.
public interface RulesetService {

    /// Load a specific ruleset by its version number.
    /// @implSpec Rulesets are cached for performance.
    Optional<Ruleset> load(Version version);

}
