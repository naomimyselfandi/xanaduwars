package io.github.naomimyselfandi.xanaduwars.core.common;

import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.testing.TestUtils;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.*;

@ExtendWith({MockitoExtension.class, SeededRandomExtension.class})
class IffTest {

    @EnumSource
    @ParameterizedTest
    void testToString(Iff iff) {
        assertThat(iff).hasToString(switch (iff) {
            case OWN -> "Own";
            case ALLY -> "Ally";
            case ENEMY -> "Enemy";
            case NEUTRAL -> "Neutral";
        });
    }

    @EnumSource
    @ParameterizedTest
    void json(Iff iff) {
        TestUtils.assertJson(iff, "\"%s\"".formatted(iff));
    }

}
