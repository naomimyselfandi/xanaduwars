package io.github.naomimyselfandi.xanaduwars.core.scripting.evaluator;

import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.core.scripting.*;
import io.github.naomimyselfandi.xanaduwars.testing.SeededRng;
import lombok.Data;
import lombok.SneakyThrows;
import org.intellij.lang.annotations.Language;
import org.jetbrains.annotations.Nullable;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

@ExtendWith(SeededRandomExtension.class)
class ScriptingIntegrationTest {

    @Test
    void looping() {
        assertThat(evaluate("""
            [
              "#sum = 0",
              "for #i in {1, 2, 3, 4, 5}:",
              "  #sum = #sum + #i",
              "return #sum"
            ]
            """)).isEqualTo(15);
    }

    @Data
    private static class TestOrdinalHolder {
        private TestOrdinal result;
        private Object foo, bar;
    }

    @ParameterizedTest
    @CsvSource(textBlock = """
            true,true
            true,false
            false,true
            false,false
            """)
    void ordinals(boolean fooIsOrdinal, boolean barIsOrdinal) {
        @Language("json") var scriptJson = """
            [
              "subject[0].result = subject[0].foo + subject[0].bar",
              "return subject[0]"
            ]
            """;
        var holder = new TestOrdinalHolder()
                .setFoo(fooIsOrdinal ? new TestOrdinal(40) : 40)
                .setBar(barIsOrdinal ? new TestOrdinal(2) : 2);
        assertThat(evaluate(scriptJson, holder)).isEqualTo(holder);
        assertThat(holder.result).isEqualTo(new TestOrdinal(42));
    }

    @ParameterizedTest
    @CsvSource(textBlock = """
            true,true
            true,false
            false,true
            false,false
            """)
    void ordinalsAsArguments(boolean fooIsOrdinal, boolean barIsOrdinal) {
        @Language("json") var scriptJson = """
            [
              "subject[0].setResult(subject[0].foo + subject[0].bar)",
              "return subject[0]"
            ]
            """;
        var holder = new TestOrdinalHolder()
                .setFoo(fooIsOrdinal ? new TestOrdinal(40) : 40)
                .setBar(barIsOrdinal ? new TestOrdinal(2) : 2);
        assertThat(evaluate(scriptJson, holder)).isEqualTo(holder);
        assertThat(holder.result).isEqualTo(new TestOrdinal(42));
    }

    @Test
    void resultToBoolean(SeededRng random) {
        @Language("json") var scriptJson = """
            [
              "#list = new ArrayList()",
              "for #i in subject:",
              "  #list.add(!!#i)",
              "return #list"
            ]
            """;
        assertThat(evaluate(scriptJson, Result.okay(), random.<Result.Fail>get())).isEqualTo(List.of(true, false));
    }

    @ParameterizedTest
    @CsvSource(textBlock = """
            distinct(),toList()
            distinct,list
            """)
    void autoStream(String distinct, String toList) {
        @Language("json") var scriptJson = """
                "return subject.%s.%s"
                """;
        scriptJson = scriptJson.formatted(distinct, toList);
        assertThat(evaluate(scriptJson, 1, 2, 3, 3, 2, 1)).isEqualTo(List.of(1, 2, 3));
    }

    @ParameterizedTest
    @ValueSource(strings = {"T(Stream).of(3, 4)", "T(Stream).of(3), T(Stream).of(4)", "3, 4"})
    void join(String join) {
        @Language("json") var scriptJson = """
                "return subject.join(%s).collect(T(Collectors).toSet())"
                """;
        scriptJson = scriptJson.formatted(join);
        assertThat(evaluate(scriptJson, 1, 2)).isEqualTo(Set.of(1, 2, 3, 4));
    }

    @ParameterizedTest
    @ValueSource(strings = {"T(Stream).of(3, 4)", "T(Stream).of(3), T(Stream).of(4)", "3, 4"})
    void drop(String drop) {
        @Language("json") var scriptJson = """
                "return subject.drop(%s).collect(T(Collectors).toSet())"
                """;
        scriptJson = scriptJson.formatted(drop);
        assertThat(evaluate(scriptJson, 1, 2, 3, 4, 5)).isEqualTo(Set.of(1, 2, 5));
    }

    @Test
    void lambdas() {
        @Language("json") var scriptJson = """
            [
              "lambda #trim:Function(#s):",
              "  return #s.trim()",
            
              "lambda #notEmpty:Predicate(#s):",
              "  return !#s.empty",
            
              "lambda #reverseOrder:Comparator(#a, #b):",
              "  return #b.compareTo(#a)",
            
              "lambda #concat:BinaryOperator(#a, #b):",
              "  return #a + #b",
            
              "return subject _",
              "  .join(' ') _",
              "  .map(#trim) _",
              "  .filter(#notEmpty) _",
              "  .sorted(#reverseOrder) _",
              "  .reduce(#concat) _",
              "  ?: 'unknown'"
            ]
            """;
        assertThat(evaluate(scriptJson, "foo", "bar")).isEqualTo("foobar");
        assertThat(evaluate(scriptJson)).isEqualTo("unknown");
    }

    @SneakyThrows
    private static @Nullable Object evaluate(@Language("json") String scriptJson, Object... payload) {
        var query = new BarEvent(List.of(payload));
        var context = new ContextFactoryImpl().create(mock(), query, mock());
        var script = new ObjectMapper().readValue(scriptJson, Script.class);
        return script.run(context, Object.class);
    }

}
