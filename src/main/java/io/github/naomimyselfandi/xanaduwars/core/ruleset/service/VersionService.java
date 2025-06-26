package io.github.naomimyselfandi.xanaduwars.core.ruleset.service;

import java.util.SequencedCollection;

/// A service that helps work with different versions of the game.
public interface VersionService {

    /// Get the most recent published version.
    Version current();

    /// Get all published versions, beginning with the most recent.
    SequencedCollection<Version> published();

    /// Get all available versions, beginning with the most recent.
    SequencedCollection<Version> all();

}
