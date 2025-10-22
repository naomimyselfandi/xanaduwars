package io.github.naomimyselfandi.xanaduwars.core.impl;

import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.core.message.Event;
import io.github.naomimyselfandi.xanaduwars.core.message.Query;
import io.github.naomimyselfandi.xanaduwars.core.model.Element;
import io.github.naomimyselfandi.xanaduwars.core.model.GameState;
import io.github.naomimyselfandi.xanaduwars.testing.SeededRng;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith({MockitoExtension.class, SeededRandomExtension.class})
abstract class GameStateAwareTest<T extends Element> {

    private boolean initialized;

    @Mock
    GameState gameState;

    T fixture;

    SeededRng random;

    @BeforeEach
    void setGameState(SeededRng random) {
        this.random = random;
        this.fixture = create();
    }

    void initializeFixture() {
        if (!initialized) {
            initialized = true;
            fixture.initialize(gameState);
        }
    }

    interface Getter<T, V> {
        V get(T element);
    }

    interface Setter<T, V> {
        Object set(T element, V value);
    }

    @SafeVarargs
    final <V> void testGetter(Getter<T, V> getter, Query<V> query, V... values) {
        initializeFixture();
        List<V> list;
        if (values.length == 0) {
            list = Stream.generate(() -> random.get(values)).distinct().limit(2).toList();
        } else {
            list = List.of(values);
        }
        for (var value : list) {
            when(gameState.evaluate(query)).thenReturn(value);
            assertThat(getter.get(fixture)).isEqualTo(value);
        }
    }

    <V> void testSetter(
            Getter<T, V> getter,
            Setter<T, V> setter,
            @Nullable V oldValue,
            @Nullable V newValue,
            Event... events
    ) {
        initializeFixture();
        assertThat(getter.get(fixture)).isEqualTo(oldValue);
        assertThat(setter.set(fixture, newValue)).isSameAs(fixture);
        if (events.length == 0) {
            verify(gameState, atLeastOnce()).clearQueryCache();
        }
        for (var event : events) {
            verify(gameState, atLeastOnce()).dispatch(event);
        }
        clearInvocations(gameState);
        assertThat(setter.set(fixture, newValue)).isSameAs(fixture);
        verify(gameState, never()).clearQueryCache();
        verify(gameState, never()).dispatch(any());
    }

    abstract T create();

}
