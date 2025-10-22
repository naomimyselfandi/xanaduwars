package io.github.naomimyselfandi.xanaduwars.core.loading;

import io.github.naomimyselfandi.xanaduwars.core.model.Version;
import io.github.naomimyselfandi.xanaduwars.core.model.VersionNumber;

import java.io.IOException;

/// An internal service that loads versions from JSON strings.
interface VersionLoader {

    /// Load a version from a JSON string.
    Version load(VersionNumber versionNumber, String json) throws IOException;

}
