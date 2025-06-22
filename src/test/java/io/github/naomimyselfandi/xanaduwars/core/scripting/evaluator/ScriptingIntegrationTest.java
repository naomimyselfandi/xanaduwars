package io.github.naomimyselfandi.xanaduwars.core.scripting.evaluator;

import io.github.naomimyselfandi.xanaduwars.core.scripting.*;
import io.github.naomimyselfandi.xanaduwars.testing.LogicalSource;
import lombok.SneakyThrows;
import org.intellij.lang.annotations.Language;
import org.jetbrains.annotations.Nullable;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

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

    @ParameterizedTest
    @LogicalSource(LogicalSource.Op.OR)
    void ordinals(boolean fooIsOrdinal, boolean barIsOrdinal, boolean resultIsOrdinal) {
        @Language("json") var scriptJson = """
            [
              "#foo = (subject[0] ? T(TestOrdinal).of(40) : 40)",
              "#bar = (subject[1] ? T(TestOrdinal).of(2) : 2)",
              "return #foo + #bar"
            ]
            """;
        scriptJson = scriptJson.replaceAll("TestOrdinal", TestOrdinal.class.getName());
        var expected = resultIsOrdinal ? TestOrdinal.of(42) : 42;
        assertThat(evaluate(scriptJson, fooIsOrdinal, barIsOrdinal)).isEqualTo(expected);
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
              "lambda #default:Supplier():",
              "  return 'unknown'",
            
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
              "  .orElseGet(#default)"
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
