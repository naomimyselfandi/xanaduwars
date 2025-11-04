package io.github.naomimyselfandi.xanaduwars.core.loading;

import com.fasterxml.jackson.databind.JsonNode;
import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.core.model.Actor;
import io.github.naomimyselfandi.xanaduwars.core.model.CommandException;
import io.github.naomimyselfandi.xanaduwars.core.model.Unit;
import io.github.naomimyselfandi.xanaduwars.testing.LogicalSource;
import io.github.naomimyselfandi.xanaduwars.testing.SeededRng;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.function.BiPredicate;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith({MockitoExtension.class, SeededRandomExtension.class})
class TargetRestrictorTest {

    @Mock
    private Unit actor;

    @Mock
    private Target<Object> base;

    @Mock
    private BiPredicate<Actor, Object> furtherValidation;

    private final  TargetRestrictor<Object> fixture = new TargetRestrictor<>() {

        @Override
        public @NotNull Target<Object> base() {
            return base;
        }

        @Override
        public boolean validateFurther(@NotNull Actor actor, @NotNull Object target) {
            return furtherValidation.test(actor, target);
        }

    };

    @Test
    void unpack(SeededRng random) throws CommandException {
        var jsonNode = random.<JsonNode>get();
        var target = new Object();
        when(base.unpack(actor, jsonNode)).thenReturn(target);
        assertThat(fixture.unpack(actor, jsonNode)).isEqualTo(target);
    }

    @ParameterizedTest
    @LogicalSource(LogicalSource.Op.AND)
    void validate(boolean baseMatches, boolean furtherValidationMatches, boolean expected) {
        var target = new Object();
        lenient().when(base.validate(actor, target)).thenReturn(baseMatches);
        lenient().when(furtherValidation.test(actor, target)).thenReturn(furtherValidationMatches);
        assertThat(fixture.validate(actor, target)).isEqualTo(expected);
    }

    @Test
    void propose() {
        var foo = new Object();
        var bar = new Object();
        when(base.propose(actor)).then(_ -> Stream.of(foo, bar));
        when(furtherValidation.test(actor, foo)).thenReturn(true);
        when(furtherValidation.test(actor, bar)).thenReturn(false);
        assertThat(fixture.propose(actor)).containsExactly(foo);
    }

    @Test
    void pack(SeededRng random) {
        var foo = new Object();
        var bar = random.<JsonNode>get();
        when(base.pack(foo)).thenReturn(bar);
        assertThat(fixture.pack(foo)).isEqualTo(bar);
    }

}
