package io.github.naomimyselfandi.xanaduwars.gameplay.rulesets.rules;

import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.gameplay.GameState;
import io.github.naomimyselfandi.xanaduwars.gameplay.Player;
import io.github.naomimyselfandi.xanaduwars.gameplay.Tile;
import io.github.naomimyselfandi.xanaduwars.gameplay.Unit;
import io.github.naomimyselfandi.xanaduwars.gameplay.queries.VisionQuery;
import io.github.naomimyselfandi.xanaduwars.gameplay.queries.VisionQueryStage;
import io.github.naomimyselfandi.xanaduwars.testing.LogicalSource;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@MockitoSettings(strictness = Strictness.LENIENT)
@ExtendWith({MockitoExtension.class, SeededRandomExtension.class})
class VisionRuleTest {

    @Mock
    private GameState gameState;

    @Mock
    private Player subject;

    @Mock
    private Tile tile;

    @Mock
    private Unit target, unit;

    @InjectMocks
    private VisionRule fixture;

    @ParameterizedTest
    @LogicalSource(LogicalSource.Op.OR)
    void isValid(boolean fromTile, boolean fromUnit, boolean expected) {
        when(subject.gameState()).thenReturn(gameState);
        when(subject.tiles()).then(_ -> Stream.of(tile));
        when(subject.units()).then(_ -> Stream.of(unit));
        when(gameState.evaluate(new VisionQueryStage(tile, target))).thenReturn(fromTile);
        when(gameState.evaluate(new VisionQueryStage(unit, target))).thenReturn(fromUnit);
        assertThat(fixture.isValid(new VisionQuery(subject, target))).isEqualTo(expected);
    }

}
