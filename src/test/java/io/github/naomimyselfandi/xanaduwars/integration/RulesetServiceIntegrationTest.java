package io.github.naomimyselfandi.xanaduwars.integration;

import io.github.naomimyselfandi.xanaduwars.gameplay.RulesetService;
import io.github.naomimyselfandi.xanaduwars.gameplay.VersionService;
import io.github.naomimyselfandi.xanaduwars.gameplay.common.Version;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.stream.Stream;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatCode;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class RulesetServiceIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private VersionService versionService;

    @Autowired
    private RulesetService rulesetService;

    @MethodSource
    @ParameterizedTest
    void allPublishedVersionsLoad(Version version) {
        assertThatCode(() -> rulesetService.load(version)).doesNotThrowAnyException();
    }

    private Stream<Version> allPublishedVersionsLoad() {
        return versionService.stream().filter(Version::isPublished);
    }

}
