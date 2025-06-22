package io.github.naomimyselfandi.xanaduwars.core.ruleset.service;

import java.util.SequencedCollection;

/// A service that helps work with different versions of the game.
public interface VersionService {

    /// All published versions, beginning with the most recent.
    SequencedCollection<Version> published();

    /// All available versions, beginning with the most recent.
    SequencedCollection<Version> all();

}
