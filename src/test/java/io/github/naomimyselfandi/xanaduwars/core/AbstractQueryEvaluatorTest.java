package io.github.naomimyselfandi.xanaduwars.core;

import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class AbstractQueryEvaluatorTest {

    @Mock
    private Consumer<Rule<?, ?>> callback;

    private record FooQuery(UUID foo) implements Query<Integer> {

        @Override
        public boolean shouldShortCircuit(Integer value) {
            return value < 0;
        }

    }

    private record BarQuery() implements Query<Integer> {}

    private record TestValidation(UUID uuid) implements Validation {}

    @Mock
    private Rule<FooQuery, Integer> globalFooRule1, globalFooRule2, unusedFooRule, contextualFooRule;

    @Mock
    private Rule<BarQuery, Integer> globalBarRule, contextualBarRule;

    @Mock
    private Rule<TestValidation, Boolean> validationRule1, validationRule2;

    private final UUID foo = UUID.randomUUID();

    private final AbstractQueryEvaluator fixture = new AbstractQueryEvaluator() {

        @Override
        protected Stream<Rule<?, ?>> globalRules() {
            return Stream.of(
                    globalFooRule1,
                    globalBarRule,
                    globalFooRule2,
                    unusedFooRule,
                    validationRule1,
                    validationRule2
            );
        }

        @Override
        protected Stream<Rule<?, ?>> contextualRules(Query<?> query) {
            if (query instanceof FooQuery(var f) && f.equals(foo)) {
                return Stream.of(contextualFooRule, contextualBarRule);
            } else {
                return Stream.empty();
            }
        }

    };

    @BeforeEach
    void setup() {
        for (var rule : List.of(globalFooRule1, globalFooRule2, unusedFooRule, contextualFooRule)) {
            when(rule.queryType()).thenReturn(FooQuery.class);
        }
        for (var rule : List.of(globalBarRule, contextualBarRule)) {
            when(rule.queryType()).thenReturn(BarQuery.class);
        }
        for (var rule : List.of(validationRule1, validationRule2)) {
            when(rule.queryType()).thenReturn(TestValidation.class);
        }
    }

    @Test
    void evaluate() {
        var query = new FooQuery(foo);
        when(globalFooRule1.handles(query, 1)).thenReturn(true);
        when(globalFooRule1.handle(query, 1)).thenReturn(2);
        when(globalFooRule2.handles(query, 2)).thenReturn(true);
        when(globalFooRule2.handle(query, 2)).thenReturn(3);
        when(contextualFooRule.handles(query, 3)).thenReturn(true);
        when(contextualFooRule.handle(query, 3)).thenReturn(4);
        when(unusedFooRule.handles(any(), any())).thenReturn(false);
        assertThat(fixture.evaluate(query, 1)).isEqualTo(4);
        verify(unusedFooRule, never()).handle(any(), any());
        verify(globalBarRule, never()).handle(any(), any());
        verify(globalBarRule, never()).handles(any(), any());
        verify(contextualBarRule, never()).handle(any(), any());
        verify(contextualBarRule, never()).handles(any(), any());
    }

    @Test
    void evaluate_RespectsShortCircuiting() {
        var query = new FooQuery(foo);
        when(globalFooRule1.handles(query, 1)).thenReturn(true);
        when(globalFooRule1.handle(query, 1)).thenReturn(-2);
        assertThat(fixture.evaluate(query, 1)).isEqualTo(-2);
        verify(globalFooRule2, never()).handle(any(), any());
        verify(contextualFooRule, never()).handle(any(), any());
        verify(unusedFooRule, never()).handle(any(), any());
        verify(globalBarRule, never()).handle(any(), any());
        verify(globalBarRule, never()).handles(any(), any());
        verify(contextualBarRule, never()).handle(any(), any());
        verify(contextualBarRule, never()).handles(any(), any());
    }

    @Test
    void evaluate_RespectsInitialShortCircuiting() {
        assertThat(fixture.evaluate(new FooQuery(foo), -1)).isEqualTo(-1);
        verifyNoInteractions(globalFooRule1, globalFooRule2, unusedFooRule, contextualFooRule);
    }

    @NullSource
    @ParameterizedTest
    @ValueSource(ints = {0, 1})
    void evaluate_Validation(@Nullable Integer index) {
        var query = new TestValidation(UUID.randomUUID());
        var rules = List.of(validationRule1, validationRule2);
        for (var i = 0; i < rules.size(); i++) {
            when(rules.get(i).handles(query, true)).thenReturn(true);
            when(rules.get(i).handle(query, true)).thenReturn(!Objects.equals(index, i));
        }
        assertThat(fixture.evaluate(query)).isEqualTo(index == null);
    }

    @NullSource
    @ParameterizedTest
    @ValueSource(ints = {0, 1})
    void evaluate_CanCaptureValidationFailures(@Nullable Integer index) {
        var query = new TestValidation(UUID.randomUUID());
        var rules = List.of(validationRule1, validationRule2);
        for (var i = 0; i < rules.size(); i++) {
            when(rules.get(i).handles(query, true)).thenReturn(true);
            when(rules.get(i).handle(query, true)).thenReturn(!Objects.equals(index, i));
        }
        if (index == null) {
            assertThat(fixture.evaluate(query, callback)).isTrue();
            verifyNoInteractions(callback);
        } else {
            assertThat(fixture.evaluate(query, callback)).isFalse();
            verify(callback).accept(rules.get(index));
        }
    }

}
