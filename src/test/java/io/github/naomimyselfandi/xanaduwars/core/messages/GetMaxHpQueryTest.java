package io.github.naomimyselfandi.xanaduwars.core.messages;

import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.core.model.Unit;
import io.github.naomimyselfandi.xanaduwars.core.model.UnitType;
import io.github.naomimyselfandi.xanaduwars.core.script.ScriptRuntime;
import io.github.naomimyselfandi.xanaduwars.testing.SeededRng;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.Mockito.*;

@ExtendWith({MockitoExtension.class, SeededRandomExtension.class})
class GetMaxHpQueryTest {

    @Mock
    private ScriptRuntime runtime;

    @Mock
    private UnitType unitType;

    @Mock
    private Unit unit;

    @InjectMocks
    private GetMaxHpQuery fixture;

    @Test
    void defaultValue(SeededRng random) {
        var value = random.nextInt();
        when(unit.getType()).thenReturn(unitType);
        when(unitType.getMaxHp()).thenReturn(value);
        assertThat(fixture.defaultValue(runtime)).isEqualTo(value);
        verifyNoInteractions(runtime);
    }

}
