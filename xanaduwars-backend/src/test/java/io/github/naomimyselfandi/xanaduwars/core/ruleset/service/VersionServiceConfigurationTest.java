package io.github.naomimyselfandi.xanaduwars.core.ruleset.service;

import io.github.naomimyselfandi.xanaduwars.core.ruleset.model.Version;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

class VersionServiceConfigurationTest {

    @Test
    void versionService() {
        var expected = new VersionServiceImpl(
                List.of(new Version("1.2.3-Foo"), new Version("1.2.3"), new Version("1.2.2-Foo"), new Version("1.2.2")),
                List.of(new Version("1.2.3"), new Version("1.2.2"))
        );
        assertThat(new VersionServiceConfiguration().versionService("testRuleset")).isEqualTo(expected);
    }

}
