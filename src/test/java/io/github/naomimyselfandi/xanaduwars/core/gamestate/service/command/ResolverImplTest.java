package io.github.naomimyselfandi.xanaduwars.core.gamestate.service.command;

import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.core.common.Kind;
import io.github.naomimyselfandi.xanaduwars.core.common.TargetSpec;
import io.github.naomimyselfandi.xanaduwars.core.gamestate.*;
import io.github.naomimyselfandi.xanaduwars.core.gamestate.dto.PathRefDto;
import io.github.naomimyselfandi.xanaduwars.core.gamestate.dto.PhysicalRefDto;
import io.github.naomimyselfandi.xanaduwars.core.gamestate.dto.PlayerRefDto;
import io.github.naomimyselfandi.xanaduwars.testing.LogicalSource;
import io.github.naomimyselfandi.xanaduwars.testing.SeededRng;
import io.github.naomimyselfandi.xanaduwars.util.ConflictException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@MockitoSettings(strictness = Strictness.LENIENT)
@ExtendWith({MockitoExtension.class, SeededRandomExtension.class})
class ResolverImplTest {

    @Mock
    private Player activePlayer, inactivePlayer;

    @Mock
    private GameState gameState;

    @Mock
    private Structure structure;

    @Mock
    private Tile tile;

    @Mock
    private Unit unit;

    private SeededRng random;

    private ObjectResolverImpl fixture;

    @BeforeEach
    void setup(SeededRng random) {
        when(gameState.getActivePlayer()).thenReturn(activePlayer);
        this.fixture = new ObjectResolverImpl();
        this.random = random;
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void resolveActor_Player(boolean active) throws ConflictException {
        var reference = new PlayerRefDto(active ? 0 : 1);
        when(gameState.getPlayers()).thenReturn(List.of(activePlayer, inactivePlayer));
        when(activePlayer.getOwner()).thenReturn(activePlayer);
        when(inactivePlayer.getOwner()).thenReturn(inactivePlayer);
        if (active) {
            assertThat(fixture.resolveActor(gameState, reference)).isEqualTo(activePlayer);
        } else {
            assertThatThrownBy(() -> fixture.resolveActor(gameState, reference)).isInstanceOf(ConflictException.class);
        }
    }

    @ParameterizedTest
    @LogicalSource(LogicalSource.Op.AND)
    void resolveActor_Structure(
            boolean inRange,
            boolean exists,
            boolean visible,
            boolean active,
            boolean ok
    ) throws ConflictException {
        var reference = new PhysicalRefDto(PhysicalRefDto.Kind.STRUCTURE, random.nextInt(), random.nextInt());
        if (inRange) createTile(reference);
        if (exists) createStructure();
        when(activePlayer.canSee(structure)).thenReturn(visible);
        when(structure.getOwner()).thenReturn(active ? activePlayer : inactivePlayer);
        if (ok) {
            assertThat(fixture.resolveActor(gameState, reference)).isEqualTo(structure);
        } else {
            assertThatThrownBy(() -> fixture.resolveActor(gameState, reference)).isInstanceOf(ConflictException.class);
        }
    }

    @Test
    void resolveActor_Tile() {
        var reference = new PhysicalRefDto(PhysicalRefDto.Kind.TILE, random.nextInt(), random.nextInt());
        createTile(reference);
        assertThatThrownBy(() -> fixture.resolveActor(gameState, reference))
                .isInstanceOf(ConflictException.class)
                .hasMessage("Cannot issue commands to that object.");
    }

    @ParameterizedTest
    @LogicalSource(LogicalSource.Op.AND)
    void resolveActor_Unit(
            boolean inRange,
            boolean exists,
            boolean visible,
            boolean active,
            boolean ok
    ) throws ConflictException {
        var reference = new PhysicalRefDto(PhysicalRefDto.Kind.UNIT, random.nextInt(), random.nextInt());
        if (inRange) createTile(reference);
        if (exists) createUnit();
        when(activePlayer.canSee(unit)).thenReturn(visible);
        when(unit.getOwner()).thenReturn(active ? activePlayer : inactivePlayer);
        if (ok) {
            assertThat(fixture.resolveActor(gameState, reference)).isEqualTo(unit);
        } else {
            assertThatThrownBy(() -> fixture.resolveActor(gameState, reference)).isInstanceOf(ConflictException.class);
        }
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void resolveTarget_Path(boolean correctType) throws ConflictException {
        var reference = random.<PathRefDto>get();
        var spec = new TargetSpec(Map.of(), random.get(), random.get(), correctType);
        if (correctType) {
            assertThat(fixture.resolveTarget(gameState, reference, spec)).isEqualTo(reference.path());
        } else {
            assertThatThrownBy(() -> fixture.resolveTarget(gameState, reference, spec))
                    .isInstanceOf(ConflictException.class);
        }
    }

    @ParameterizedTest
    @LogicalSource(LogicalSource.Op.AND)
    void resolveTarget_Structure(
            boolean inRange,
            boolean exists,
            boolean visible,
            boolean correctType,
            boolean ok
    ) throws ConflictException {
        var reference = new PhysicalRefDto(PhysicalRefDto.Kind.STRUCTURE, random.nextInt(), random.nextInt());
        var spec = new TargetSpec(Map.of(Kind.STRUCTURE, correctType), random.get(), random.get(), random.get());
        if (inRange) createTile(reference);
        if (exists) createStructure();
        when(activePlayer.canSee(structure)).thenReturn(visible);
        if (ok) {
            assertThat(fixture.resolveTarget(gameState, reference, spec)).isEqualTo(structure);
        } else {
            assertThatThrownBy(() -> fixture.resolveTarget(gameState, reference, spec))
                    .isInstanceOf(ConflictException.class);
        }
    }

    @ParameterizedTest
    @LogicalSource(LogicalSource.Op.AND)
    void resolveTarget_Tile(boolean inRange, boolean correctType, boolean ok) throws ConflictException {
        var reference = new PhysicalRefDto(PhysicalRefDto.Kind.TILE, random.nextInt(), random.nextInt());
        var spec = new TargetSpec(Map.of(Kind.TILE, correctType), random.get(), random.get(), random.get());
        if (inRange) createTile(reference);
        if (ok) {
            assertThat(fixture.resolveTarget(gameState, reference, spec)).isEqualTo(tile);
        } else {
            assertThatThrownBy(() -> fixture.resolveTarget(gameState, reference, spec))
                    .isInstanceOf(ConflictException.class);
        }
    }

    @ParameterizedTest
    @LogicalSource(LogicalSource.Op.AND)
    void resolveTarget_Unit(
            boolean inRange,
            boolean exists,
            boolean visible,
            boolean correctType,
            boolean ok
    ) throws ConflictException {
        var reference = new PhysicalRefDto(PhysicalRefDto.Kind.UNIT, random.nextInt(), random.nextInt());
        var spec = new TargetSpec(Map.of(Kind.UNIT, correctType), random.get(), random.get(), random.get());
        if (inRange) createTile(reference);
        if (exists) createUnit();
        when(activePlayer.canSee(unit)).thenReturn(visible);
        if (ok) {
            assertThat(fixture.resolveTarget(gameState, reference, spec)).isEqualTo(unit);
        } else {
            assertThatThrownBy(() -> fixture.resolveTarget(gameState, reference, spec))
                    .isInstanceOf(ConflictException.class);
        }
    }

    private void createStructure() {
        when(tile.getStructure()).thenReturn(structure);
    }

    private void createTile(PhysicalRefDto reference) {
        when(gameState.getTiles()).thenReturn(new TreeMap<>(Map.of(new TileId(reference.x(), reference.y()), tile)));
    }

    private void createUnit() {
        when(tile.getUnit()).thenReturn(unit);
    }

}
