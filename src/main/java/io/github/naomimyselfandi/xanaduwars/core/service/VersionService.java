package io.github.naomimyselfandi.xanaduwars.core.service;

import io.github.naomimyselfandi.xanaduwars.core.model.Version;
import io.github.naomimyselfandi.xanaduwars.core.model.VersionNumber;
import org.jetbrains.annotations.Unmodifiable;
import org.springframework.data.domain.Pageable;

import java.util.List;

/// A service for working with game versions.
public interface VersionService {

    /// Get a page of version numbers. This reports both published and internal
    /// version numbers.
    @Unmodifiable List<VersionNumber> getVersionNumbers(Pageable page);

    /// Get a paged of published version numbers.
    @Unmodifiable List<VersionNumber> getPublishedVersionNumbers(Pageable page);

    /// Get a version by its version number.
    Version getVersion(VersionNumber versionNumber);

}
