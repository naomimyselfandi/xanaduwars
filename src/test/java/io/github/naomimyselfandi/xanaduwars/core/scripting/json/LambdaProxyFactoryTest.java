package io.github.naomimyselfandi.xanaduwars.core.scripting.json;

import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.core.scripting.Script;
import io.github.naomimyselfandi.xanaduwars.testing.SeededRng;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.expression.EvaluationContext;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith({MockitoExtension.class, SeededRandomExtension.class})
class LambdaProxyFactoryTest {

    private interface Helper {

        String frobnicate(String foo, String bar, String baz);

        default String frobnicate(String string) {
            var parts = string.split(",");
            return frobnicate(parts[0], parts[1], parts[2]);
        }

    }

    private String name;

    @Mock
    private Script script;

    @Mock
    private EvaluationContext context;

    private List<String> parameters;

    @BeforeEach
    void setup(SeededRng random) {
        name = random.nextString();
        parameters = List.of(random.get(), random.get(), random.get());
    }

    @Test
    void create(SeededRng random) {
        var foo = random.nextString();
        var bar = random.nextString();
        var baz = random.nextString();
        var result = random.nextString();
        var proxy = (Helper) LambdaProxyFactory.create(name, script, context, parameters, Helper.class);
        var lambdaContext = new LambdaContext(context, parameters, List.of(foo, bar, baz));
        when(script.run(lambdaContext, String.class)).thenReturn(result);
        assertThat(proxy.frobnicate(foo, bar, baz)).isEqualTo(result);
    }

    @Test
    void create_DefaultMethods(SeededRng random) {
        var foo = random.nextString();
        var bar = random.nextString();
        var baz = random.nextString();
        var result = random.nextString();
        var proxy = (Helper) LambdaProxyFactory.create(name, script, context, parameters, Helper.class);
        var lambdaContext = new LambdaContext(context, parameters, List.of(foo, bar, baz));
        when(script.run(lambdaContext, String.class)).thenReturn(result);
        assertThat(proxy.frobnicate("%s,%s,%s".formatted(foo, bar, baz))).isEqualTo(result);
    }

    @Test
    @SuppressWarnings("EqualsWithItself")
    void create_Equals() {
        var proxy = LambdaProxyFactory.create(name, script, context, parameters, Helper.class);
        assertThat(proxy).isEqualTo(proxy);
        assertThat(proxy).isNotEqualTo(LambdaProxyFactory.create(name, script, context, parameters, Helper.class));
        assertThat(proxy).isNotEqualTo(new Object());
    }

    @Test
    void create_HashCode() {
        var proxy = LambdaProxyFactory.create(name, script, context, parameters, Helper.class);
        assertThat(proxy.hashCode()).isEqualTo(System.identityHashCode(proxy));
    }

    @Test
    void create_ToString() {
        var proxy = LambdaProxyFactory.create(name, script, context, parameters, Helper.class);
        assertThat(proxy).hasToString("%s:Helper:%d", name, System.identityHashCode(proxy));
    }

}
