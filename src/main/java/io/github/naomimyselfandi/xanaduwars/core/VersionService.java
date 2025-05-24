package io.github.naomimyselfandi.xanaduwars.core;

import io.github.naomimyselfandi.xanaduwars.core.wrapper.VersionNumber;

import java.util.stream.Stream;

/// A service that helps work with different versions of the game.
public interface VersionService {

    /// The current public version.
    VersionNumber current();

    /// Stream all available versions, starting with the latest version.
    Stream<VersionNumber> stream();

    /// Load a specific ruleset by its version number.
    /// @implSpec Rulesets are cached for performance.
    Ruleset load(VersionNumber version);

}
