package io.github.naomimyselfandi.xanaduwars.core.ruleset.service;

import io.github.naomimyselfandi.xanaduwars.core.ruleset.model.Version;

import java.util.List;

/// A service that helps work with different versions of the game.
public interface VersionService {

    /// All published versions, beginning with the most recent.
    List<Version> published();

    /// All available versions, beginning with the most recent.
    List<Version> all();

}
