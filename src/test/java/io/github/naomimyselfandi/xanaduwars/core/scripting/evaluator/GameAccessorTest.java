package io.github.naomimyselfandi.xanaduwars.core.scripting.evaluator;

import io.github.naomimyselfandi.xanaduwars.core.scripting.Query;
import io.github.naomimyselfandi.xanaduwars.core.scripting.QueryEvaluator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.expression.AccessException;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.TypedValue;

import static org.assertj.core.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class GameAccessorTest {

    @Mock
    private QueryEvaluator game;

    @Mock
    private EvaluationContext context;

    @Mock
    private Query<?> query;

    private GameAccessor fixture;

    @BeforeEach
    void setup() {
        fixture = new GameAccessor(new TypedValue(game));
    }

    @ParameterizedTest
    @CsvSource(textBlock = """
            game,true
            Game,false
            g4m3,false
            """)
    void canRead(String name, boolean expected) {
        assertThat(fixture.canRead(context, query, name)).isEqualTo(expected);
    }

    @NullSource
    @ParameterizedTest
    @ValueSource(strings = {"foobar"})
    void canRead_WhenTheTargetIsNotAQuery_ThenFalse(Object target) {
        assertThat(fixture.canRead(context, target, "game")).isFalse();
    }

    @Test
    void read() {
        assertThat(fixture.read(context, query, "game")).isEqualTo(new TypedValue(game));
    }

    @Test
    void canWrite() {
        assertThat(fixture.canWrite(context, query, "game")).isFalse();
    }

    @Test
    void write() {
        assertThatThrownBy(() -> fixture.write(context, query, "game", new Object()))
                .isInstanceOf(AccessException.class);
    }

    @Test
    void getSpecificTargetClasses() {
        assertThat(fixture.getSpecificTargetClasses()).containsExactly(Query.class);
    }

}
