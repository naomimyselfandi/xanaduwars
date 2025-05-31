package io.github.naomimyselfandi.xanaduwars.gameplay.rulesets.controller;

import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.account.value.Role;
import io.github.naomimyselfandi.xanaduwars.auth.dto.UserDetailsDto;
import io.github.naomimyselfandi.xanaduwars.gameplay.*;
import io.github.naomimyselfandi.xanaduwars.gameplay.rulesets.dto.*;
import io.github.naomimyselfandi.xanaduwars.testing.SeededRng;
import org.jetbrains.annotations.Nullable;
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

import java.util.List;
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
    private RulesetDtoMapper rulesetDtoMapper;

    @InjectMocks
    private RulesetController fixture;

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void get(boolean authenticated, SeededRng random) {
        var version = random.nextPublishedVersion();
        when(rulesetService.load(version)).thenReturn(Optional.of(ruleset));
        var dto = new RulesetDto();
        dto.setVersion(random.nextVersion());
        dto.setCommanders(List.of(new CommanderDto()));
        dto.setSpellTypes(List.of(new SpellTypeDto()));
        dto.setTileTypes(List.of(new TileTypeDto()));
        dto.setUnitTypes(List.of(new UnitTypeDto()));
        when(rulesetDtoMapper.convert(ruleset)).thenReturn(dto);
        var user = user(authenticated);
        assertThat(fixture.get(user, version)).isEqualTo(ResponseEntity.ok(dto));
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void get_WhenTheRulesetDoesNotExist_ThenReturnsA404(boolean authenticated, SeededRng random) {
        var user = user(authenticated);
        assertThat(fixture.get(user, random.nextVersion())).isEqualTo(ResponseEntity.notFound().build());
    }

    @Test
    void get_WhenTheVersionIsInternalAndTheUserIsADeveloper_ThenOk(SeededRng random) {
        var version = random.nextPublishedVersion();
        when(rulesetService.load(version)).thenReturn(Optional.of(ruleset));
        var dto = new RulesetDto();
        dto.setVersion(random.nextInternalVersion());
        dto.setCommanders(List.of(new CommanderDto()));
        dto.setSpellTypes(List.of(new SpellTypeDto()));
        dto.setTileTypes(List.of(new TileTypeDto()));
        dto.setUnitTypes(List.of(new UnitTypeDto()));
        when(rulesetDtoMapper.convert(ruleset)).thenReturn(dto);
        var user = new UserDetailsDto();
        user.setAuthorities(List.of(Role.DEVELOPER));
        assertThat(fixture.get(user, version)).isEqualTo(ResponseEntity.ok(dto));
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void get_WhenTheVersionIsInternalAndTheUserIsNotADeveloper_ThenReturnsA404(boolean authenticated, SeededRng random) {
        var user = user(authenticated);
        assertThat(fixture.get(user, random.nextInternalVersion())).isEqualTo(ResponseEntity.notFound().build());
    }

    private @Nullable UserDetailsDto user(boolean authenticated) {
        if (authenticated) {
            var user = new UserDetailsDto();
            user.setAuthorities(List.of());
            return user;
        } else {
            return null;
        }
    }

}
