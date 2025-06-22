package io.github.naomimyselfandi.xanaduwars.core.scripting.evaluator;

import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.expression.AccessException;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.TypedValue;
import org.springframework.expression.spel.support.ReflectiveMethodResolver;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.util.*;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.InstanceOfAssertFactories.*;

@ExtendWith({MockitoExtension.class, SeededRandomExtension.class})
class IterableMethodResolverTest {

    private EvaluationContext context;

    private IterableMethodResolver fixture;

    @BeforeEach
    void setup() {
        context = new StandardEvaluationContext();
        fixture = new IterableMethodResolver(new ReflectiveMethodResolver());
    }

    @Test
    void resolve_Iterable() throws AccessException {
        var target = (Iterable<Integer>) List.of(1, 2, 3)::iterator;
        var predicate = (Predicate<Integer>) i -> (i & 1) == 1;
        var executor = fixture.resolve(context, target, "filter", List.of(TypeDescriptor.forObject(predicate)));
        assertThat(executor).isNotNull();
        assertThat(executor.execute(context, target, predicate))
                .extracting(TypedValue::getValue)
                .isInstanceOf(Stream.class)
                .asInstanceOf(STREAM)
                .containsExactly(1, 3);
    }

    @Test
    void resolve_Collection() throws AccessException {
        var target = List.of(1, 2, 3);
        var function = (UnaryOperator<Integer>) i -> i * i;
        var executor = fixture.resolve(context, target, "map", List.of(TypeDescriptor.forObject(function)));
        assertThat(executor).isNotNull();
        assertThat(executor.execute(context, target, function))
                .extracting(TypedValue::getValue)
                .isInstanceOf(Stream.class)
                .asInstanceOf(STREAM)
                .containsExactly(1, 4, 9);
    }

    @Test
    void resolve_Stream() throws AccessException {
        var target = Stream.of(3, 2, 1);
        var executor = fixture.resolve(context, target, "sorted", List.of());
        assertThat(executor).isNotNull();
        assertThat(executor.execute(context, target))
                .extracting(TypedValue::getValue)
                .isInstanceOf(Stream.class)
                .asInstanceOf(STREAM)
                .containsExactly(1, 2, 3);
    }

    @Test
    void resolve_Map() throws AccessException {
        var target = Map.of(1, 4, 2, 3);
        var comparator = Map.Entry.comparingByValue();
        var executor = fixture.resolve(context, target, "sorted", List.of(TypeDescriptor.forObject(comparator)));
        assertThat(executor).isNotNull();
        assertThat(executor.execute(context, target, comparator))
                .extracting(TypedValue::getValue)
                .isInstanceOf(Stream.class)
                .asInstanceOf(STREAM)
                .containsExactly(Map.entry(2, 3), Map.entry(1, 4));
    }

    @Test
    void resolve_Join() throws AccessException {
        var target = List.of(1, 2);
        var executor = fixture.resolve(context, target, "join", List.of());
        assertThat(executor).isNotNull();
        assertThat(executor.execute(context, target, Set.of(3), 4, Optional.of(5), null, Stream.of(6)))
                .extracting(TypedValue::getValue)
                .isInstanceOf(Stream.class)
                .asInstanceOf(STREAM)
                .containsExactly(1, 2, 3, 4, 5, 6);
    }

    @Test
    void resolve_Drop() throws AccessException {
        var target = List.of(1, 2, 3, 4, 5, 6);
        var executor = fixture.resolve(context, target, "drop", List.of());
        assertThat(executor).isNotNull();
        assertThat(executor.execute(context, target, 3, 4, Stream.of(6)))
                .extracting(TypedValue::getValue)
                .isInstanceOf(Stream.class)
                .asInstanceOf(STREAM)
                .containsExactly(1, 2, 5);
    }

    @Test
    void resolve_List() throws AccessException {
        var target = List.of(1, 2);
        var executor = fixture.resolve(context, target, "list", List.of());
        assertThat(executor).isNotNull();
        assertThat(executor.execute(context, target, 3, Stream.of(4), List.of(5, 6)))
                .extracting(TypedValue::getValue)
                .isInstanceOf(ArrayList.class)
                .asInstanceOf(LIST)
                .containsExactly(1, 2, 3, 4, 5, 6);
    }

    @Test
    void resolve_Set() throws AccessException {
        var target = List.of(1, 2, 3);
        var executor = fixture.resolve(context, target, "set", List.of());
        assertThat(executor).isNotNull();
        assertThat(executor.execute(context, target, 3, Stream.of(4), List.of(5, 6)))
                .extracting(TypedValue::getValue)
                .isInstanceOf(HashSet.class)
                .asInstanceOf(SET)
                .containsOnly(1, 2, 3, 4, 5, 6);
    }

    @Test
    void resolve_WhenTheMethodDoesNotExist_ThenNull() throws AccessException {
        assertThat(fixture.resolve(context, List.of(), "xyzzy", List.of())).isNull();
    }

    @MethodSource
    @ParameterizedTest
    void resolve_WhenTheTargetIsNotIterableLike_ThenNull(Object target) throws AccessException {
        assertThat(fixture.resolve(context, target, "sorted", List.of())).isNull();
    }

    private static Stream<Object> resolve_WhenTheTargetIsNotIterableLike_ThenNull() {
        return Stream.of("foo", Optional.of("bar"), Optional.empty());
    }

}
