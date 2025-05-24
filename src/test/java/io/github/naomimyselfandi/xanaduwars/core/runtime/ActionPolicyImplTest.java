package io.github.naomimyselfandi.xanaduwars.core.runtime;

import io.github.naomimyselfandi.seededrandom.SeededRandom;
import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.core.*;
import io.github.naomimyselfandi.xanaduwars.core.wrapper.Percent;
import io.github.naomimyselfandi.xanaduwars.ext.None;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@MockitoSettings(strictness = Strictness.LENIENT)
@ExtendWith({MockitoExtension.class, SeededRandomExtension.class})
class ActionPolicyImplTest {

    @Mock
    private UnitType unitType;

    @Mock
    private SpellType<?> spellType;

    @Mock
    private Ruleset ruleset;

    @Mock
    private Action<Player, None> passAction, resignAction;

    @Mock
    private Action<Tile, UnitType> deployAction;

    @Mock
    private Action<Unit, List<Direction>> moveAction;

    @Mock
    private Action<Unit, Direction> dropAction;

    @Mock
    private Action<Unit, Node> attackAction;

    @Mock
    private Action<Unit, None> waitAction, otherAction;

    @Mock
    private Player player;

    @Mock
    private Spell spell;

    @Mock
    private Tile tile;

    @Mock
    private Unit unit, cargo;

    private final ActionPolicyImpl fixture = new ActionPolicyImpl();

    @Test
    void actions_Player() {
        when(player.knownSpells()).thenReturn(List.of(spellType));
        when(ruleset.passAction()).thenReturn(passAction);
        when(ruleset.resignAction()).thenReturn(resignAction);
        assertThat(fixture.actions(ruleset, player)).containsExactly(spellType, passAction, resignAction);
    }

    @Test
    void actions_Spell() {
        assertThat(fixture.actions(ruleset, spell)).isEmpty();
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void actions_Tile(boolean canDeploy) {
        when(tile.deploymentRoster()).thenReturn(canDeploy ? Set.of(unitType) : Set.of());
        when(ruleset.deployAction()).thenReturn(deployAction);
        if (canDeploy) {
            assertThat(fixture.actions(ruleset, tile)).containsExactly(deployAction);
        } else {
            assertThat(fixture.actions(ruleset, tile)).isEmpty();
        }
    }

    @ParameterizedTest
    @CsvSource(textBlock = """
            true,true,true
            false,true,true
            true,false,true
            true,true,false
            false,false,true
            true,false,true
            true,true,false
            false,true,false
            false,false,false
            """)
    void actions_Unit(boolean canMove, boolean canShoot, boolean canDrop, SeededRandom random) {
        var damageTable = canShoot
                ? Map.<NodeType, Percent>of(unitType, Percent.withDoubleValue(random.nextDouble()))
                : Map.<NodeType, Percent>of();
        when(ruleset.moveAction()).thenReturn(moveAction);
        when(ruleset.dropAction()).thenReturn(dropAction);
        when(ruleset.attackAction()).thenReturn(attackAction);
        when(ruleset.waitAction()).thenReturn(waitAction);
        when(unit.speed()).thenReturn(canMove ? random.nextInt(1, 10) : 0);
        when(unit.damageTable()).thenReturn(damageTable);
        when(unit.cargo()).thenReturn(canDrop ? List.of(cargo) : List.of());
        when(unit.abilities()).thenReturn(List.of(otherAction));
        var expected = new ArrayList<>();
        if (canMove) expected.add(moveAction);
        if (canShoot) expected.add(attackAction);
        if (canDrop) expected.add(dropAction);
        expected.addAll(List.of(otherAction, waitAction));
        assertThat(fixture.actions(ruleset, unit)).isEqualTo(expected);
    }

}
