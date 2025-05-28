package io.github.naomimyselfandi.xanaduwars.gameplay;

import io.github.naomimyselfandi.xanaduwars.gameplay.common.Version;

import java.util.Optional;
import java.util.stream.Stream;

/// A service that helps work with different versions of the game.
public interface VersionService {

    /// The current public version.
    Version current();

    /// Stream all available versions, starting with the latest version.
    Stream<Version> stream();

}
