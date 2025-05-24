package io.github.naomimyselfandi.xanaduwars.testing;

import io.github.naomimyselfandi.xanaduwars.core.Ruleset;
import io.github.naomimyselfandi.xanaduwars.core.VersionService;
import io.github.naomimyselfandi.xanaduwars.core.wrapper.VersionNumber;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.util.stream.Stream;

// Placeholder "implementation" so our integration tests can start
@Service
class TemporaryVersionService implements VersionService {

    @Override
    public @NotNull VersionNumber current() {
        throw new UnsupportedOperationException();
    }

    @Override
    public @NotNull Stream<VersionNumber> stream() {
        throw new UnsupportedOperationException();
    }

    @Override
    public @NotNull Ruleset load(@NotNull VersionNumber version) {
        throw new UnsupportedOperationException();
    }

}
