package io.github.naomimyselfandi.xanaduwars.core.messages;

import io.github.naomimyselfandi.xanaduwars.core.model.Player;
import io.github.naomimyselfandi.xanaduwars.core.model.Unit;
import io.github.naomimyselfandi.xanaduwars.core.model.UnitSelector;
import io.github.naomimyselfandi.xanaduwars.core.script.ScriptRuntime;
import io.github.naomimyselfandi.xanaduwars.testing.LogicalSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class CanBoardQueryTest {

    @Mock
    private Player player, anotherPlayer;

    @Mock
    private ScriptRuntime runtime;

    @Mock
    private UnitSelector hangar;

    @Mock
    private Unit passenger, transport, anotherUnit;

    private CanBoardQuery fixture;

    @BeforeEach
    void setup() {
        fixture = new CanBoardQuery(passenger, transport);
    }

    @ParameterizedTest
    @LogicalSource(LogicalSource.Op.AND)
    void defaultValue(boolean sameOwner, boolean empty, boolean selectorMatches, boolean expected) {
        when(passenger.getOwner()).thenReturn(player);
        when(transport.getOwner()).thenReturn(sameOwner ? player : anotherPlayer);
        when(transport.getUnit()).thenReturn(empty ? null : anotherUnit);
        when(transport.getHangar()).thenReturn(hangar);
        when(hangar.test(passenger)).thenReturn(selectorMatches);
        assertThat(fixture.defaultValue(runtime)).isEqualTo(expected);
        verifyNoInteractions(runtime);
    }

}
