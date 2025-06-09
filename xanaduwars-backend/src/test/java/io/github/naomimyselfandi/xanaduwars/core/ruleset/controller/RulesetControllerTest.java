package io.github.naomimyselfandi.xanaduwars.core.ruleset.controller;

import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.core.ruleset.model.Ruleset;
import io.github.naomimyselfandi.xanaduwars.core.ruleset.service.RulesetService;
import io.github.naomimyselfandi.xanaduwars.testing.SeededRng;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith({MockitoExtension.class, SeededRandomExtension.class})
class RulesetControllerTest {

    @Mock
    private Ruleset ruleset;

    @Mock
    private RulesetService rulesetService;

    @InjectMocks
    private RulesetController fixture;

    @Test
    void get(SeededRng random) {
        var version = random.nextVersion();
        when(rulesetService.load(version)).thenReturn(ruleset);
        assertThat(fixture.get(version)).isEqualTo(ResponseEntity.ok(ruleset));
    }

    @Test
    void get_WhenTheRulesetDoesNotExist_ThenReturnsA404(SeededRng random) {
        var version = random.nextVersion();
        when(rulesetService.load(version)).thenReturn(null);
        assertThat(fixture.get(version)).isEqualTo(ResponseEntity.notFound().build());
    }

}
