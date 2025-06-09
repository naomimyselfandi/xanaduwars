package io.github.naomimyselfandi.xanaduwars.core.gameplay.queries;

import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.core.gameplay.state.Structure;
import io.github.naomimyselfandi.xanaduwars.core.gameplay.state.Tile;
import io.github.naomimyselfandi.xanaduwars.core.gameplay.state.Unit;
import io.github.naomimyselfandi.xanaduwars.testing.SeededRng;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith({MockitoExtension.class, SeededRandomExtension.class})
class DefenseQueryTest {

    @Mock
    private Structure structureSubject;

    @Mock
    private Tile tile;

    @Mock
    private Unit unitSubject, attacker;

    @Test
    void defaultValue_WhenTheSubjectIsAUnit_ThenDelegatesToTheTerrain(SeededRng random) {
        var cover = random.nextDouble();
        when(unitSubject.terrain()).thenReturn(tile);
        when(tile.cover()).thenReturn(cover);
        assertThat(new DefenseQuery(unitSubject, attacker).defaultValue()).isEqualTo(cover);
    }

    @Test
    void defaultValue_WhenTheSubjectIsAStructure_ThenZero() {
        assertThat(new DefenseQuery(structureSubject, attacker).defaultValue()).isZero();
    }

    @Test
    void defaultValue_ToleratesUnitsInOtherUnits() {
        assertThat(new DefenseQuery(unitSubject, attacker).defaultValue()).isZero();
    }

}
