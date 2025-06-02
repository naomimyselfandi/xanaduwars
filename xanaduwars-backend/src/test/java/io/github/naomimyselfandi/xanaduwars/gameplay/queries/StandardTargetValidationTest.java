package io.github.naomimyselfandi.xanaduwars.gameplay.queries;

import io.github.naomimyselfandi.xanaduwars.gameplay.*;
import io.github.naomimyselfandi.xanaduwars.gameplay.rulesets.actions.ActionWithFilter;
import io.github.naomimyselfandi.xanaduwars.gameplay.rulesets.filter.BiFilter;
import io.github.naomimyselfandi.xanaduwars.testing.LogicalSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class StandardTargetValidationTest {

    @Mock
    private GameState gameState;

    @Mock
    private Action<Actor, Object> action;

    @Mock
    private ActionWithFilter<Actor, Object> actionWithFilter;

    @Mock
    private BiFilter<Actor, Object> filter;

    @Mock
    private Player player;

    @Mock
    private Unit subject, anotherUnit;

    @Mock
    private Tile tile;

    @BeforeEach
    void setup() {
        when(subject.gameState()).thenReturn(gameState);
    }

    @ParameterizedTest
    @LogicalSource(LogicalSource.Op.AND)
    void defaultValue(boolean hasAction, boolean structurallyValid, boolean expected) {
        var target = new Object();
        when(gameState.actions(subject)).thenReturn(List.of(hasAction ? action : actionWithFilter));
        when(action.test(subject, target)).thenReturn(structurallyValid);
        var validation = new StandardTargetValidation<>(action, subject, target);
        assertThat(validation.defaultValue()).isEqualTo(expected);
    }

    @ParameterizedTest
    @LogicalSource(LogicalSource.Op.AND)
    void defaultValue(boolean hasAction, boolean structurallyValid, boolean passesFilter, boolean expected) {
        var target = new Object();
        when(gameState.actions(subject)).thenReturn(List.of(hasAction ? actionWithFilter : action));
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
        when(gameState.actions(subject)).thenReturn(List.of(actionWithFilter));
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
