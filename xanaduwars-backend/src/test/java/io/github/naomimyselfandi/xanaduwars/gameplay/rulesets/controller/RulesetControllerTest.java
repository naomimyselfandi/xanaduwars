package io.github.naomimyselfandi.xanaduwars.gameplay.rulesets.controller;

import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.gameplay.*;
import io.github.naomimyselfandi.xanaduwars.gameplay.rulesets.dto.*;
import io.github.naomimyselfandi.xanaduwars.testing.SeededRng;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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

    @Test
    void get(SeededRng random) {
        var version = random.nextPublishedVersion();
        when(rulesetService.load(version)).thenReturn(Optional.of(ruleset));
        var dto = new RulesetDto();
        dto.setVersion(random.nextVersion());
        dto.setCommanders(List.of(new CommanderDto()));
        dto.setSpellTypes(List.of(new SpellTypeDto()));
        dto.setTileTypes(List.of(new TileTypeDto()));
        dto.setUnitTypes(List.of(new UnitTypeDto()));
        when(rulesetDtoMapper.convert(ruleset)).thenReturn(dto);
        assertThat(fixture.get(version)).isEqualTo(ResponseEntity.ok(dto));
    }

    @Test
    void get_WhenTheRulesetDoesNotExist_ThenReturnsA404(SeededRng random) {
        assertThat(fixture.get(random.nextVersion())).isEqualTo(ResponseEntity.notFound().build());
    }

}
