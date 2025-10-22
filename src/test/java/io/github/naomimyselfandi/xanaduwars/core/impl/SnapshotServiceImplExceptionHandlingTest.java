package io.github.naomimyselfandi.xanaduwars.core.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.core.model.GameState;
import io.github.naomimyselfandi.xanaduwars.core.service.Snapshot;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.*;

@ExtendWith({MockitoExtension.class, SeededRandomExtension.class})
class SnapshotServiceImplExceptionHandlingTest {

    // Not the most important tests in the world, but we aim for full coverage.
    // SnapshotServiceImplTest (unavoidably) has a lot in it, so the exception
    // handlers get their own test class to avoid adding noise. Lucky them.

    @Mock
    private GameState gameState;

    private SnapshotServiceImpl fixture;

    @BeforeEach
    void setup() {
        // Jackson's exceptions expect some contextual information that's hard
        // to mock up. It's easier to give invalid data to a real ObjectMapper.
        fixture = new SnapshotServiceImpl(new ObjectMapper());
    }

    @Test
    void load() {
        assertThatThrownBy(() -> fixture.load(new Snapshot("this is not json")))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Failed loading game state snapshot!")
                .hasCauseInstanceOf(JsonProcessingException.class);
    }

    @Test
    void save() {
        assertThatThrownBy(() -> fixture.save(gameState))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Failed saving game state snapshot!")
                .hasCauseInstanceOf(JsonProcessingException.class);
    }

}
