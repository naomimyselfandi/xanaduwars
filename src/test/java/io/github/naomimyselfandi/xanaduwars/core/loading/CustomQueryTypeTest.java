package io.github.naomimyselfandi.xanaduwars.core.loading;

import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.core.script.Script;
import io.github.naomimyselfandi.xanaduwars.core.script.ScriptRuntime;
import io.github.naomimyselfandi.xanaduwars.testing.SeededRng;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

@ExtendWith({MockitoExtension.class, SeededRandomExtension.class})
class CustomQueryTypeTest {

    @Mock
    private ScriptRuntime runtime;

    @Mock
    private Script script;

    private CustomQueryType fixture;

    @BeforeEach
    void setup(SeededRng random) {
        var name = random.nextString();
        var foo = random.not(name);
        var bar = random.not(name, foo);
        fixture = new CustomQueryType(name, List.of(foo, bar), script);
    }

    @Test
    void call() {
        var fooValue = new Object();
        var barValue = new Object();
        assertThat(fixture.call(fooValue, barValue)).isEqualTo(new CustomQuery(fixture, List.of(fooValue, barValue)));
    }

    @Test
    void testToString() {
        assertThat(fixture).hasToString(fixture.name());
    }

}
