package io.github.naomimyselfandi.xanaduwars.core.messages;

import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.core.model.Tile;
import io.github.naomimyselfandi.xanaduwars.core.model.TileType;
import io.github.naomimyselfandi.xanaduwars.core.script.ScriptRuntime;
import io.github.naomimyselfandi.xanaduwars.testing.SeededRng;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith({MockitoExtension.class, SeededRandomExtension.class})
class GetCoverQueryTest {

    @Mock
    private ScriptRuntime runtime;

    @Mock
    private TileType tileType;

    @Mock
    private Tile tile;

    @InjectMocks
    private GetCoverQuery fixture;

    @Test
    void defaultValue(SeededRng random) {
        var value = random.nextDouble();
        when(tile.getType()).thenReturn(tileType);
        when(tileType.getCover()).thenReturn(value);
        assertThat(fixture.defaultValue(runtime)).isEqualTo(value);
        verifyNoInteractions(runtime);
    }

}
