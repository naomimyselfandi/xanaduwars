package io.github.naomimyselfandi.xanaduwars.core.messages;

import io.github.naomimyselfandi.xanaduwars.core.model.Ability;
import io.github.naomimyselfandi.xanaduwars.core.model.Commander;
import io.github.naomimyselfandi.xanaduwars.core.script.ScriptRuntime;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class AreSpellChoicesOkQueryTest {

    @Mock
    private ScriptRuntime runtime;

    @Mock
    private Commander commander;

    @Mock
    private Ability foo, bar, baz;

    @MethodSource
    @ParameterizedTest
    void defaultValue(List<String> choiceNames, List<Boolean> choiceStatus, int max, boolean expected) {
        when(commander.getChosenSpells()).thenReturn(max);
        var choices = choiceNames.stream().map(Map.of("foo", foo, "bar", bar, "baz", baz)::get).toList();
        for (var i = 0; i < choices.size(); i++) {
            when(choices.get(i).isSpellChoice()).thenReturn(choiceStatus.get(i));
        }
        assertThat(new AreSpellChoicesOkQuery(commander, choices).defaultValue(runtime)).isEqualTo(expected);
        verifyNoInteractions(runtime);
    }

    private static Stream<Arguments> defaultValue() {
        return Stream.of(
                arguments(List.of("foo", "bar"), List.of(true, true), 2, true),
                arguments(List.of("foo", "bar"), List.of(true, false), 2, false),
                arguments(List.of("foo", "bar"), List.of(false, true), 2, false),
                arguments(List.of("foo"), List.of(true), 1, true),
                arguments(List.of("foo"), List.of(true), 2, true),
                arguments(List.of("foo", "bar"), List.of(true, true), 1, false),
                arguments(List.of("foo", "bar", "baz"), List.of(true, true, true), 2, false),
                arguments(List.of("baz", "baz"), List.of(true, true), 2, false)
        );
    }

    @Test
    void constructor() {
        var choices = new ArrayList<Ability>();
        choices.add(foo);
        choices.add(bar);
        var query = new AreSpellChoicesOkQuery(commander, choices);
        assertThat(query.choices()).isUnmodifiable().containsExactly(foo, bar);
        choices.add(baz);
        assertThat(query.choices()).isUnmodifiable().containsExactly(foo, bar);
    }

}
