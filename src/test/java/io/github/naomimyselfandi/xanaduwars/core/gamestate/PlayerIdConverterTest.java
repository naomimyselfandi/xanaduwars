package io.github.naomimyselfandi.xanaduwars.core.gamestate;

import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.testing.SeededRng;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.*;

@ExtendWith(SeededRandomExtension.class)
class PlayerIdConverterTest {

    @Test
    void convert(SeededRng random) {
        var id = random.nextInt();
        assertThat(new PlayerIdConverter().convert(String.valueOf(id))).isEqualTo(new PlayerId(id));
    }

}
