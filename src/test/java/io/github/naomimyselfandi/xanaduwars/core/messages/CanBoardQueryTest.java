package io.github.naomimyselfandi.xanaduwars.core.messages;

import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.core.model.Player;
import io.github.naomimyselfandi.xanaduwars.core.model.Unit;
import io.github.naomimyselfandi.xanaduwars.core.model.UnitTag;
import io.github.naomimyselfandi.xanaduwars.core.script.ScriptRuntime;
import io.github.naomimyselfandi.xanaduwars.testing.LogicalSource;
import io.github.naomimyselfandi.xanaduwars.testing.SeededRng;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@MockitoSettings(strictness = Strictness.LENIENT)
@ExtendWith({MockitoExtension.class, SeededRandomExtension.class})
class CanBoardQueryTest {

    @Mock
    private Player player, anotherPlayer;

    @Mock
    private ScriptRuntime runtime;

    @Mock
    private Unit passenger, transport, anotherUnit;

    private CanBoardQuery fixture;

    private SeededRng random;

    @BeforeEach
    void setup(SeededRng random) {
        fixture = new CanBoardQuery(passenger, transport);
        this.random = random;
    }

    @ParameterizedTest
    @LogicalSource(LogicalSource.Op.AND)
    void defaultValue(boolean sameOwner, boolean empty, boolean tagMatches, boolean expected) {
        when(passenger.getOwner()).thenReturn(player);
        when(transport.getOwner()).thenReturn(sameOwner ? player : anotherPlayer);
        when(transport.getUnit()).thenReturn(empty ? null : anotherUnit);
        var foo = random.<UnitTag>get();
        var bar = random.not(foo);
        var baz = random.not(foo, bar);
        var bat = tagMatches ? random.pick(foo, bar) : random.not(foo, bar, baz);
        when(passenger.getTags()).thenReturn(List.of(foo, bar));
        when(transport.getHangar()).thenReturn(List.of(baz, bat));
        assertThat(fixture.defaultValue(runtime)).isEqualTo(expected);
        verifyNoInteractions(runtime);
    }

}
