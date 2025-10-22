package io.github.naomimyselfandi.xanaduwars.core.script;

import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.testing.SeededRng;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.*;

@ExtendWith({MockitoExtension.class, SeededRandomExtension.class})
class FunctionTest {

    @Mock
    private ScriptRuntime runtime;

    private Function fixture;

    @BeforeEach
    void setup() {
        fixture = "(%s,%s),(%s,%s)"::formatted;
    }

    @Test
    void bind(SeededRng random) {
        var foo = random.nextString();
        var bar = random.nextString();
        var baz = random.nextString();
        var bat = random.nextString();
        assertThat(fixture.bind(foo, bar).call(baz, bat))
                .isEqualTo("(%s,%s),(%s,%s)".formatted(foo, bar, baz, bat));
    }

}
