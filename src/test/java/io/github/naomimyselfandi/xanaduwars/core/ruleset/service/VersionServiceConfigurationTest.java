package io.github.naomimyselfandi.xanaduwars.core.ruleset.service;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class VersionServiceConfigurationTest {

    @Test
    void versionService() {
        var v000 = new Version("0.0.0");
        var v000bar = new Version("0.0.0-Bar");
        var v000foo = new Version("0.0.0-Foo");
        var v123 = new Version("1.2.3");
        var service = new VersionServiceConfiguration().versionService("testRuleset");
        assertThat(service.all()).containsExactly(v123, v000foo, v000bar, v000);
        assertThat(service.published()).containsExactly(v123, v000);
        assertThat(service.current()).isEqualTo(v123);
    }

}
