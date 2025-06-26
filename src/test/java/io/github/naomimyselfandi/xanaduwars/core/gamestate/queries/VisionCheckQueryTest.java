package io.github.naomimyselfandi.xanaduwars.core.gamestate.queries;

import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.core.gamestate.*;
import io.github.naomimyselfandi.xanaduwars.testing.LogicalSource;
import io.github.naomimyselfandi.xanaduwars.testing.SeededRng;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@MockitoSettings(strictness = Strictness.LENIENT)
@ExtendWith({MockitoExtension.class, SeededRandomExtension.class})
class VisionCheckQueryTest {

    @Mock
    private GameState gameState;

    @Mock
    private Player subject;

    @Mock
    private Structure structure, structure0, structure1, enemyStructure, neutralStructure;

    @Mock
    private Tile tile;

    @Mock
    private Unit unit, unit0, unit1, enemyUnit, neutralUnit;

    private SeededRng random;

    @BeforeEach
    void setup(SeededRng random) {
        this.random = random;
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void defaultValue_WhenTheTargetIsAFriendlyUnit_ThenTrue(boolean friendly) {
        when(subject.isAlly(unit)).thenReturn(friendly);
        assertThat(new VisionCheckQuery(subject, unit).defaultValue()).isEqualTo(friendly);
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void defaultValue_WhenTheTargetIsAFriendlyStructure_ThenTrue(boolean friendly) {
        when(subject.isAlly(structure)).thenReturn(friendly);
        assertThat(new VisionCheckQuery(subject, structure).defaultValue()).isEqualTo(friendly);
    }

    @ParameterizedTest
    @LogicalSource(LogicalSource.Op.OR)
    void defaultValue_WhenTheTargetIsATile_ThenChecksAssets(
            boolean unit0CanSee,
            boolean unit1CanSee,
            boolean structure0CanSee,
            boolean structure1CanSee,
            boolean expected
    ) {
        test(tile, unit0CanSee, unit1CanSee, structure0CanSee, structure1CanSee, expected);
    }

    @ParameterizedTest
    @LogicalSource(LogicalSource.Op.OR)
    void defaultValue_WhenTheTargetIsAUnit_ThenChecksAssets(
            boolean unit0CanSee,
            boolean unit1CanSee,
            boolean structure0CanSee,
            boolean structure1CanSee,
            boolean expected
    ) {
        when(subject.isAlly(unit)).thenReturn(false);
        test(unit, unit0CanSee, unit1CanSee, structure0CanSee, structure1CanSee, expected);
    }

    @ParameterizedTest
    @LogicalSource(LogicalSource.Op.OR)
    void defaultValue_WhenTheTargetIsAStructure_ThenChecksAssets(
            boolean unit0CanSee,
            boolean unit1CanSee,
            boolean structure0CanSee,
            boolean structure1CanSee,
            boolean expected
    ) {
        when(subject.isAlly(structure)).thenReturn(false);
        test(structure, unit0CanSee, unit1CanSee, structure0CanSee, structure1CanSee, expected);
    }

    private void test(
            Physical target,
            boolean unit0CanSee,
            boolean unit1CanSee,
            boolean structure0CanSee,
            boolean structure1CanSee,
            boolean expected
    ) {
        when(target.getTile()).thenReturn(Optional.of(tile));
        when(tile.getGameState()).thenReturn(gameState);
        when(subject.isAlly(unit0)).thenReturn(true);
        when(subject.isAlly(unit1)).thenReturn(true);
        when(subject.isAlly(structure0)).thenReturn(true);
        when(subject.isAlly(structure1)).thenReturn(true);
        when(subject.isAlly(enemyUnit)).thenReturn(false);
        when(subject.isAlly(neutralUnit)).thenReturn(false);
        when(subject.isAlly(enemyStructure)).thenReturn(false);
        when(subject.isAlly(neutralStructure)).thenReturn(false);
        when(gameState.evaluate(new AssetVisionQuery(unit0, tile))).thenReturn(unit0CanSee);
        when(gameState.evaluate(new AssetVisionQuery(unit1, tile))).thenReturn(unit1CanSee);
        when(gameState.evaluate(new AssetVisionQuery(structure0, tile))).thenReturn(structure0CanSee);
        when(gameState.evaluate(new AssetVisionQuery(structure1, tile))).thenReturn(structure1CanSee);
        when(gameState.getUnits()).thenReturn(new TreeMap<>(Map.of(
                random.get(), unit0,
                random.get(), unit1,
                random.get(), enemyUnit,
                random.get(), neutralUnit
        )));
        when(gameState.getStructures()).thenReturn(new TreeMap<>(Map.of(
                random.get(), structure0,
                random.get(), structure1,
                random.get(), enemyStructure,
                random.get(), neutralStructure
        )));
        assertThat(new VisionCheckQuery(subject, target).defaultValue()).isEqualTo(expected);
    }

}
