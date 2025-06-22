package io.github.naomimyselfandi.xanaduwars.integration;

import io.github.naomimyselfandi.xanaduwars.core.ruleset.service.Version;
import io.github.naomimyselfandi.xanaduwars.core.ruleset.service.RulesetService;
import io.github.naomimyselfandi.xanaduwars.core.ruleset.service.VersionService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.function.Predicate;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
class RulesetIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private VersionService versionService;

    @Autowired
    private RulesetService rulesetService;

    @Test
    void allPublishedVersionsLoad() {
        var problems = new ArrayList<>();
        for (var version : versionService.published()) {
            try {
                assertThat(rulesetService.load(version)).isNotNull();
            } catch (Exception | AssertionError e) {
                log.error("Failed loading version {}!", version, e);
                problems.add(version);
            }
        }
        assertThat(problems).isEmpty();
    }

    @Test
    void recentDevelopmentVersionsLoad() {
        var problems = new ArrayList<>();
        var versions = versionService.all().stream().takeWhile(Predicate.not(Version::isPublished)).toList();
        for (var version : versions) {
            try {
                assertThat(rulesetService.load(version)).isNotNull();
            } catch (Exception | AssertionError e) {
                log.error("Failed loading version {}!", version, e);
                problems.add(version);
            }
        }
        assertThat(problems).isEmpty();
    }

}
