package io.github.naomimyselfandi.xanaduwars.info;

import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.account.entity.HumanAccount;
import io.github.naomimyselfandi.xanaduwars.auth.service.CurrentAccountService;
import io.github.naomimyselfandi.xanaduwars.gameplay.Ruleset;
import io.github.naomimyselfandi.xanaduwars.gameplay.RulesetService;
import io.github.naomimyselfandi.xanaduwars.gameplay.rulesets.dto.RulesetDto;
import io.github.naomimyselfandi.xanaduwars.testing.SeededRng;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@MockitoSettings(strictness = Strictness.LENIENT)
@ExtendWith({MockitoExtension.class, SeededRandomExtension.class})
class RulesetControllerTest {

    @Mock
    private Ruleset ruleset;

    @Mock
    private RulesetService rulesetService;

    @Mock
    private CurrentAccountService currentAccountService;

    @InjectMocks
    private RulesetController fixture;

    @RepeatedTest(2)
    void get(SeededRng random) {
        when(ruleset.version()).thenReturn(random.nextVersion());
        var version = random.nextPublishedVersion();
        when(rulesetService.load(version)).thenReturn(Optional.of(ruleset));
        assertThat(fixture.get(version)).isEqualTo(ResponseEntity.ok(new RulesetDto(ruleset)));
    }

    @Test
    void get_WhenTheRulesetDoesNotExist_ThenReturnsA404(SeededRng random) {
        assertThat(fixture.get(random.nextVersion())).isEqualTo(ResponseEntity.notFound().build());
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void get_WhenTheVersionIsInternal_ThenTheUserMustBeADeveloper(boolean developer, SeededRng random) {
        when(ruleset.version()).thenReturn(random.nextVersion());
        var version = random.nextInternalVersion();
        when(rulesetService.load(version)).thenReturn(Optional.of(ruleset));
        var account = new HumanAccount().developer(developer);
        when(currentAccountService.tryGet()).thenReturn(Optional.of(account));
        if (developer) {
            assertThat(fixture.get(version)).isEqualTo(ResponseEntity.ok(new RulesetDto(ruleset)));
        } else {
            assertThat(fixture.get(random.nextVersion())).isEqualTo(ResponseEntity.notFound().build());
        }
    }

}
