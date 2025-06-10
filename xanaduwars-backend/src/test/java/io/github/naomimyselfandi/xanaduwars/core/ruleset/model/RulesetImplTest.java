package io.github.naomimyselfandi.xanaduwars.core.ruleset.model;

import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.core.scripting.Rule;
import io.github.naomimyselfandi.xanaduwars.testing.SeededRng;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith({MockitoExtension.class, SeededRandomExtension.class})
class RulesetImplTest {

    @Mock
    private Rule foo, bar;

    @Mock
    private Commander commander0, commander1;

    @Mock
    private Spell spell0, spell1;

    @Mock
    private StructureType structureType0, structureType1;

    @Mock
    private TileType tileType0, tileType1;

    @Mock
    private UnitType unitType0, unitType1;

    private RulesetImpl fixture;

    @BeforeEach
    void setup() {
        fixture = new RulesetImpl()
                .setCommanders(List.of(commander0, commander1))
                .setSpells(List.of(spell0, spell1))
                .setStructureTypes(List.of(structureType0, structureType1))
                .setTileTypes(List.of(tileType0, tileType1))
                .setUnitTypes(List.of(unitType0, unitType1));
    }

    @Test
    void getCommander() {
        assertThat(fixture.getCommander(new CommanderId(0))).isEqualTo(commander0);
        assertThat(fixture.getCommander(new CommanderId(1))).isEqualTo(commander1);
    }

    @Test
    void getSpell() {
        assertThat(fixture.getSpell(new SpellId(0))).isEqualTo(spell0);
        assertThat(fixture.getSpell(new SpellId(1))).isEqualTo(spell1);
    }

    @Test
    void getStructureType() {
        assertThat(fixture.getStructureType(new StructureTypeId(0))).isEqualTo(structureType0);
        assertThat(fixture.getStructureType(new StructureTypeId(1))).isEqualTo(structureType1);
    }

    @Test
    void getTileTypes() {
        assertThat(fixture.getTileTypes(new TileTypeId(0))).isEqualTo(tileType0);
        assertThat(fixture.getTileTypes(new TileTypeId(1))).isEqualTo(tileType1);
    }

    @Test
    void getUnitType() {
        assertThat(fixture.getUnitType(new UnitTypeId(0))).isEqualTo(unitType0);
        assertThat(fixture.getUnitType(new UnitTypeId(1))).isEqualTo(unitType1);
    }

    @Test
    void declarations(SeededRng random) {
        var spellTag0 = random.<SpellTag>get();
        var spellTag1 = random.<SpellTag>get();
        when(spell0.getTags()).thenReturn(Set.of(spellTag0));
        when(spell1.getTags()).thenReturn(Set.of(spellTag1));
        var structureTag0 = random.<StructureTag>get();
        var structureTag1 = random.<StructureTag>get();
        when(structureType0.getTags()).thenReturn(Set.of(structureTag0));
        when(structureType1.getTags()).thenReturn(Set.of(structureTag1));
        var tileTag0 = random.<TileTag>get();
        var tileTag1 = random.<TileTag>get();
        var tileTag2 = random.<TileTag>get();
        when(tileType0.getTags()).thenReturn(Set.of(tileTag0, tileTag2));
        when(tileType1.getTags()).thenReturn(Set.of(tileTag1, tileTag2));
        var unitTag0 = random.<UnitTag>get();
        var unitTag1 = random.<UnitTag>get();
        var unitTag2 = random.<UnitTag>get();
        when(unitType0.getTags()).thenReturn(Set.of(unitTag0, unitTag2));
        when(unitType1.getTags()).thenReturn(Set.of(unitTag1, unitTag2));
        assertThat(fixture.constants()).containsOnly(
                commander0,
                commander1,
                spell0,
                spell1,
                structureType0,
                structureType1,
                tileType0,
                tileType1,
                unitType0,
                unitType1,
                spellTag0,
                spellTag1,
                structureTag0,
                structureTag1,
                tileTag0,
                tileTag2,
                tileTag1,
                unitTag0,
                unitTag2,
                unitTag1
        );
    }

    @Test
    void rules() {
        var ruleset = new RulesetImpl().setGlobalRules(List.of(foo, bar));
        assertThat(ruleset.rules()).containsExactly(foo, bar);
    }

}
