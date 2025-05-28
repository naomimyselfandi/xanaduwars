package io.github.naomimyselfandi.xanaduwars.gameplay.queries;

import io.github.naomimyselfandi.xanaduwars.gameplay.*;
import io.github.naomimyselfandi.xanaduwars.gameplay.rulesets.actions.ActionWithFilter;
import io.github.naomimyselfandi.xanaduwars.gameplay.rulesets.filter.BiFilter;
import io.github.naomimyselfandi.xanaduwars.testing.LogicalSource;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class StandardTargetValidationTest {

    @Mock
    private Action<Element, Object> action;

    @Mock
    private ActionWithFilter<Element, Object> actionWithFilter;

    @Mock
    private BiFilter<Element, Object> filter;

    @Mock
    private Player player;

    @Mock
    private Unit subject, anotherUnit;

    @Mock
    private Tile tile;

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void defaultValue(boolean expected) {
        var target = new Object();
        when(action.test(subject, target)).thenReturn(expected);
        var validation = new StandardTargetValidation<>(action, subject, target);
        assertThat(validation.defaultValue()).isEqualTo(expected);
    }

    @ParameterizedTest
    @LogicalSource(LogicalSource.Op.AND)
    void defaultValue(boolean structurallyValid, boolean passesFilter, boolean expected) {
        var target = new Object();
        when(actionWithFilter.test(subject, target)).thenReturn(structurallyValid);
        when(actionWithFilter.filter()).thenReturn(filter);
        when(filter.test(subject, target)).thenReturn(passesFilter);
        var validation = new StandardTargetValidation<>(actionWithFilter, subject, target);
        assertThat(validation.defaultValue()).isEqualTo(expected);
    }

    @ParameterizedTest
    @LogicalSource(LogicalSource.Op.AND)
    void defaultValue_SpecialUnitRules(
            boolean structurallyValid,
            boolean passesFilter,
            boolean onTile,
            boolean visible,
            boolean expected
    ) {
        when(actionWithFilter.test(subject, anotherUnit)).thenReturn(structurallyValid);
        when(actionWithFilter.filter()).thenReturn(filter);
        when(filter.test(subject, anotherUnit)).thenReturn(passesFilter);
        when(anotherUnit.tile()).thenReturn(Optional.of(tile).filter(_ -> onTile));
        when(subject.owner()).thenReturn(Optional.of(player));
        when(player.canSee(anotherUnit)).thenReturn(visible);
        var validation = new StandardTargetValidation<>(actionWithFilter, subject, anotherUnit);
        assertThat(validation.defaultValue()).isEqualTo(expected);
    }

}
