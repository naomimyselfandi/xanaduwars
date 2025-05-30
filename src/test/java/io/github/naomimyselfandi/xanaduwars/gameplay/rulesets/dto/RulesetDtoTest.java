package io.github.naomimyselfandi.xanaduwars.gameplay.rulesets.dto;

import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.gameplay.*;
import io.github.naomimyselfandi.xanaduwars.gameplay.common.TagSet;
import io.github.naomimyselfandi.xanaduwars.testing.SeededRng;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith({MockitoExtension.class, SeededRandomExtension.class})
class RulesetDtoTest {

    @Mock
    private Ruleset source;

    @Mock
    private Commander commander;

    @Mock
    private SpellType<?> spellType;

    @Mock
    private TileType tileType;

    @Mock
    private UnitType unitType;

    @Test
    void mappings(SeededRng random) {
        when(source.version()).thenReturn(random.nextVersion());
        when(commander.id()).thenReturn(random.nextCommanderId());
        when(commander.name()).thenReturn(random.nextName());
        when(commander.tags()).thenReturn(TagSet.of(random.nextTag()));
        when(source.commanders()).thenReturn(List.of(commander));
        when(spellType.id()).thenReturn(random.nextSpellTypeId());
        when(spellType.name()).thenReturn(random.nextName());
        when(spellType.tags()).thenReturn(TagSet.of(random.nextTag()));
        when(source.spellTypes()).thenReturn(List.of(spellType));
        when(tileType.id()).thenReturn(random.nextTileTypeId());
        when(tileType.name()).thenReturn(random.nextName());
        when(tileType.tags()).thenReturn(TagSet.of(random.nextTag()));
        when(source.tileTypes()).thenReturn(List.of(tileType));
        when(unitType.id()).thenReturn(random.nextUnitTypeId());
        when(unitType.name()).thenReturn(random.nextName());
        when(unitType.tags()).thenReturn(TagSet.of(random.nextTag()));
        when(source.unitTypes()).thenReturn(List.of(unitType));
        var expected = new RulesetDto();
        expected.setVersion(source.version());
        expected.setCommanders(List.of(new CommanderDto(commander)));
        expected.setSpellTypes(List.of(new SpellTypeDto(spellType)));
        expected.setTileTypes(List.of(new TileTypeDto(tileType)));
        expected.setUnitTypes(List.of(new UnitTypeDto(unitType)));
        assertThat(new RulesetDto(source)).isEqualTo(expected);
    }

}
