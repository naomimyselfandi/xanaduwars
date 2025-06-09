package io.github.naomimyselfandi.xanaduwars.core.gameplay.entity;

import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.core.ruleset.model.Name;
import io.github.naomimyselfandi.xanaduwars.testing.SeededRng;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

@ExtendWith(SeededRandomExtension.class)
class HistoryTest {

    @Test
    void names(SeededRng random) {
        var names = List.<Name>of(random.get(), random.get());
        assertThat(new History(names).names()).isEqualTo(names);
    }

}
