package io.github.naomimyselfandi.xanaduwars.core.runtime;

import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.core.*;
import io.github.naomimyselfandi.xanaduwars.core.Query;
import io.github.naomimyselfandi.xanaduwars.core.Rule;
import io.github.naomimyselfandi.xanaduwars.core.queries.SubjectQuery;
import io.github.naomimyselfandi.xanaduwars.core.queries.TargetQuery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith({MockitoExtension.class, SeededRandomExtension.class})
class QueryEvaluatorImplTest {

    private record TestSubjectQuery(Element subject) implements SubjectQuery<Object> {}

    private record TestTargetQuery(Element subject, Object target) implements TargetQuery<Object> {}

    @Mock
    private Rule<?, ?> rule;

    @Mock
    private Rule<?, ?> unitRule, unitTargetRule;

    @Mock
    private Rule<?, ?> tileRule, tileTargetRule;

    @Mock
    private Rule<?, ?> playerRule, playerTargetRule;

    @Mock
    private Rule<?, ?> spellRule, spellTargetRule;

    @Mock
    private UnitType unitType;

    @Mock
    private Unit unit;

    @Mock
    private TileType tileType;

    @Mock
    private Tile tile;

    @Mock
    private Commander playerType;

    @Mock
    private SpellType<?> spellType;

    @Mock
    private Spell spell;

    @Mock
    private Player player;

    @Mock
    private Ruleset ruleset;

    @InjectMocks
    private QueryEvaluatorImpl fixture;

    private final List<Rule<?, ?>> subjectRules = new ArrayList<>();
    private final List<Rule<?, ?>> targetRules = new ArrayList<>();

    @BeforeEach
    void setup() {
        lenient().when(unit.type()).thenReturn(unitType);
        lenient().when(unitType.rules()).thenReturn(List.of(unitRule));
        lenient().when(unitType.targetRules()).thenReturn(List.of(unitTargetRule));
        lenient().when(tile.type()).thenReturn(tileType);
        lenient().when(tileType.rules()).thenReturn(List.of(tileRule));
        lenient().when(tileType.targetRules()).thenReturn(List.of(tileTargetRule));
        lenient().when(player.type()).thenReturn(playerType);
        lenient().when(playerType.rules()).thenReturn(List.of(playerRule));
        lenient().when(playerType.targetRules()).thenReturn(List.of(playerTargetRule));
        lenient().when((Object) spell.type()).thenReturn(spellType);
        lenient().when(spellType.rules()).thenReturn(List.of(spellRule));
        lenient().when(spellType.targetRules()).thenReturn(List.of(spellTargetRule));
        lenient().when(player.activeSpells()).thenReturn(List.of(spell));
        lenient().when(spell.owner()).thenReturn(Optional.of(player));
    }

    @Test
    void globalRules() {
        when(ruleset.globalRules()).thenReturn(List.of(rule));
        assertThat(fixture.globalRules()).containsExactly(rule);
    }

    @Test
    void contextualRules() {
        assertThat(fixture.contextualRules(new Query<>() {})).isEmpty();
    }

    @ParameterizedTest
    @CsvSource(textBlock = """
            PLAYER,TILE
            PLAYER,UNIT
            NEUTRAL,TILE
            NEUTRAL,UNIT
            """)
    void contextualRules_Unit(String owner, String location) {
        subjectRules.add(unitRule);
        targetRules.add(unitTargetRule);
        if (owner.equals("PLAYER")) {
            when(unit.owner()).thenReturn(Optional.of(player));
            subjectRules.addAll(List.of(playerRule, spellRule));
            targetRules.addAll(List.of(playerTargetRule, spellTargetRule));
        }
        if (location.equals("TILE")) {
            when(unit.tile()).thenReturn(Optional.of(tile));
            subjectRules.add(tileRule);
            targetRules.add(tileTargetRule);
        }
        testContextualRules(unit);
    }

    @ParameterizedTest
    @CsvSource(textBlock = """
            PLAYER,NONE
            PLAYER,UNIT
            NEUTRAL,NONE
            NEUTRAL,UNIT
            """)
    void contextualRules_Tile(String owner, String contents) {
        subjectRules.add(tileRule);
        targetRules.add(tileTargetRule);
        if (owner.equals("PLAYER")) {
            when(tile.owner()).thenReturn(Optional.of(player));
            subjectRules.addAll(List.of(playerRule, spellRule));
            targetRules.addAll(List.of(playerTargetRule, spellTargetRule));
        }
        if (contents.equals("UNIT")) {
            when(tile.unit()).thenReturn(Optional.of(unit));
            subjectRules.add(unitRule);
            targetRules.add(unitTargetRule);
        }
        testContextualRules(tile);
    }

    @Test
    void contextualRules_Player() {
        subjectRules.addAll(List.of(playerRule, spellRule));
        targetRules.addAll(List.of(playerTargetRule, spellTargetRule));
        testContextualRules(player);
    }

    @Test
    void contextualRules_Spell() {
        subjectRules.addAll(List.of(spellRule, playerRule));
        targetRules.addAll(List.of(spellTargetRule, playerTargetRule));
        testContextualRules(spell);
        verify(player, never()).activeSpells();
    }

    @Test
    void contextualRules_NonElement() {
        assertThat(fixture.contextualRules(new TestTargetQuery(unit, new Object()))).containsExactly(unitRule);
    }

    private void testContextualRules(Element element) {
        var expected = new ArrayList<>(subjectRules);
        assertThat(fixture.contextualRules(new TestSubjectQuery(element))).containsExactlyElementsOf(expected);
        expected.addAll(targetRules);
        assertThat(fixture.contextualRules(new TestTargetQuery(element, element))).containsExactlyElementsOf(expected);
    }

}
