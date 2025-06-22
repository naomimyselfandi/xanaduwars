package io.github.naomimyselfandi.xanaduwars.core.gamestate;

import com.fasterxml.jackson.databind.DeserializationContext;
import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.testing.SeededRng;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.*;

@ExtendWith({MockitoExtension.class, SeededRandomExtension.class})
class PlayerIdKeyDeserializerTest {

    @Mock
    private DeserializationContext context;

    @InjectMocks
    private PlayerIdKeyDeserializer fixture;

    @Test
    void deserializeKey(SeededRng random) {
        var playerId = random.<PlayerId>get();
        assertThat(fixture.deserializeKey("%d".formatted(playerId.playerId()), context)).isEqualTo(playerId);
    }

}
