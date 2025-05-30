package io.github.naomimyselfandi.xanaduwars.integration;

import io.github.naomimyselfandi.xanaduwars.gameplay.RulesetService;
import io.github.naomimyselfandi.xanaduwars.gameplay.VersionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatCode;

class RulesetServiceIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private VersionService versionService;

    @Autowired
    private RulesetService rulesetService;

    @Test
    void allPublishedVersionsLoad() {
        for (var version : versionService.stream().toList()) {
            assertThatCode(() -> rulesetService.load(version)).doesNotThrowAnyException();
        }
    }

}
