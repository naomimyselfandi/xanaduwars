package io.github.naomimyselfandi.xanaduwars.core.gameplay.queries;

import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.core.gameplay.state.Structure;
import io.github.naomimyselfandi.xanaduwars.core.gameplay.state.Unit;
import io.github.naomimyselfandi.xanaduwars.core.ruleset.model.StructureType;
import io.github.naomimyselfandi.xanaduwars.core.ruleset.model.UnitType;
import io.github.naomimyselfandi.xanaduwars.testing.SeededRng;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith({MockitoExtension.class, SeededRandomExtension.class})
class VisionRangeQueryTest {

    @Mock
    private StructureType structureType;

    @Mock
    private Structure structure;

    @Mock
    private UnitType unitType;

    @Mock
    private Unit unit;

    @Test
    void defaultValue_Structure(SeededRng random) {
        var vision = random.nextInt();
        when(structureType.vision()).thenReturn(vision);
        when(structure.type()).thenReturn(structureType);
        assertThat(new VisionRangeQuery(structure).defaultValue()).isEqualTo(vision);
    }

    @Test
    void defaultValue_Unit(SeededRng random) {
        var vision = random.nextInt();
        when(unitType.vision()).thenReturn(vision);
        when(unit.type()).thenReturn(unitType);
        assertThat(new VisionRangeQuery(unit).defaultValue()).isEqualTo(vision);
    }

}
