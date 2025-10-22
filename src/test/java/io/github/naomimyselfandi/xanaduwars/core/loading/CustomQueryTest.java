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
import org.springframework.core.convert.TypeDescriptor;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith({MockitoExtension.class, SeededRandomExtension.class})
class CustomQueryTest {

    @Mock
    private ScriptRuntime runtime;

    @Mock
    private Script script;

    private String foo;
    private String bar;

    private Object fooValue, barValue;

    private CustomQuery fixture;

    @BeforeEach
    void setup(SeededRng random) {
        foo = random.nextString();
        bar = random.not(foo);
        fooValue = new Object();
        barValue = new Object();
        var type = new CustomQueryType(random.nextString(), List.of(foo, bar), script);
        fixture = new CustomQuery(type, List.of(fooValue, barValue));
    }

    @Test
    void defaultValue() {
        var value = new Object();
        when(script.executeNotNull(runtime, Map.of(foo, fooValue, bar, barValue))).thenReturn(value);
        assertThat(fixture.defaultValue(runtime)).isEqualTo(value);
    }

    @Test
    void resultType() {
        assertThat(fixture.resultType()).isEqualTo(TypeDescriptor.valueOf(Object.class));
    }

}
