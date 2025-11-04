package io.github.naomimyselfandi.xanaduwars.core.loading;

import com.fasterxml.jackson.databind.JsonNode;
import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.core.model.CommandException;
import io.github.naomimyselfandi.xanaduwars.core.model.Player;
import io.github.naomimyselfandi.xanaduwars.core.model.Tile;
import io.github.naomimyselfandi.xanaduwars.core.model.Unit;
import io.github.naomimyselfandi.xanaduwars.testing.LogicalSource;
import io.github.naomimyselfandi.xanaduwars.testing.SeededRng;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith({MockitoExtension.class, SeededRandomExtension.class})
class TargetOfUnitTest {

    @Mock
    private Player player;

    @Mock
    private Tile tile, anotherTile, yetAnotherTile;

    @Mock
    private Unit actor, unit, anotherUnit;

    @Mock
    private Target<Tile> base;

    @InjectMocks
    private TargetOfUnit fixture;

    private SeededRng random;

    @BeforeEach
    void setup(SeededRng random) {
        this.random = random;
        lenient().when(actor.asPlayer()).thenReturn(player);
    }

    @ParameterizedTest
    @LogicalSource(LogicalSource.Op.AND)
    void unpack(boolean tileHasUnit, boolean unitIsPerceived, boolean ok) throws CommandException {
        var jsonNode = random.<JsonNode>get();
        lenient().when(base.unpack(actor, jsonNode)).thenReturn(tile);
        lenient().when(tile.getUnit()).thenReturn(tileHasUnit ? unit : null);
        lenient().when(player.perceives(unit)).thenReturn(unitIsPerceived);
        if (ok) {
            assertThat(fixture.unpack(actor, jsonNode)).isEqualTo(unit);
        } else {
            assertThatThrownBy(() -> fixture.unpack(actor, jsonNode))
                    .isInstanceOf(CommandException.class)
                    .hasMessage("Target unit does not exist or is hidden.");
        }
    }

    @ParameterizedTest
    @LogicalSource(LogicalSource.Op.AND)
    void validate(boolean unitOnTile, boolean baseAcceptsTile, boolean unitIsPerceived, boolean expected) {
        lenient().when(unit.getLocation()).thenReturn(unitOnTile ? tile : anotherUnit);
        lenient().when(base.validate(actor, tile)).thenReturn(baseAcceptsTile);
        lenient().when(player.perceives(unit)).thenReturn(unitIsPerceived);
        assertThat(fixture.validate(actor, unit)).isEqualTo(expected);
    }

    @Test
    void propose() {
        when(base.propose(actor)).then(_ -> Stream.of(tile, anotherTile, yetAnotherTile));
        when(tile.getUnit()).thenReturn(unit);
        when(anotherTile.getUnit()).thenReturn(anotherUnit);
        when(player.perceives(unit)).thenReturn(false);
        when(player.perceives(anotherUnit)).thenReturn(true);
        assertThat(fixture.propose(actor)).containsExactly(anotherUnit);
    }

    @Test
    void pack(SeededRng random) {
        var jsonNode = random.<JsonNode>get();
        when(unit.getLocation()).thenReturn(tile);
        when(base.pack(tile)).thenReturn(jsonNode);
        assertThat(fixture.pack(unit)).isEqualTo(jsonNode);
    }

}
