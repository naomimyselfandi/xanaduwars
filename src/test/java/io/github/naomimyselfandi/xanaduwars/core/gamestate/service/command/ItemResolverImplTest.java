package io.github.naomimyselfandi.xanaduwars.core.gamestate.service.command;

import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.core.common.Name;
import io.github.naomimyselfandi.xanaduwars.core.common.TargetSpec;
import io.github.naomimyselfandi.xanaduwars.core.gamestate.GameState;
import io.github.naomimyselfandi.xanaduwars.core.gamestate.Unit;
import io.github.naomimyselfandi.xanaduwars.core.gamestate.dto.CommandItemDto;
import io.github.naomimyselfandi.xanaduwars.core.gamestate.dto.TargetRefDto;
import io.github.naomimyselfandi.xanaduwars.core.ruleset.NormalAction;
import io.github.naomimyselfandi.xanaduwars.testing.SeededRng;
import io.github.naomimyselfandi.xanaduwars.util.ConflictException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith({MockitoExtension.class, SeededRandomExtension.class})
class ItemResolverImplTest {

    @Mock
    private NormalAction action;

    private Name name;

    private TargetRefDto targetDto0, targetDto1;

    private TargetSpec targetSpec0, targetSpec1;

    @Mock
    private GameState gameState;

    @Mock
    private Unit actor, target0, target1;

    @Mock
    private ActionResolver actionResolver;

    @Mock
    private ObjectResolver objectResolver;

    @InjectMocks
    private ItemResolverImpl fixture;

    @BeforeEach
    void setup(SeededRng random) throws ConflictException {
        name = random.<Name>get();
        targetDto0 = random.get();
        targetDto1 = random.get();
        targetSpec0 = random.get();
        targetSpec1 = random.get();
        when(action.getTargets()).thenReturn(List.of(targetSpec0, targetSpec1));
        when(actionResolver.resolveAction(actor, name)).thenReturn(action);
    }

    @Test
    void resolveItem() throws ConflictException {
        when(objectResolver.resolveTarget(gameState, targetDto0, targetSpec0)).thenReturn(target0);
        when(objectResolver.resolveTarget(gameState, targetDto1, targetSpec1)).thenReturn(target1);
        var expected = new Command.Item(action, List.of(target0, target1));
        var dto = new CommandItemDto(name, List.of(targetDto0, targetDto1));
        assertThat(fixture.resolveItem(gameState, dto, actor)).isEqualTo(expected);
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 3})
    void resolveItem_WhenTheNumberOfTargetsIsWrong_ThenThrows(int count, SeededRng random) {
        var targets = IntStream.range(0, count).<TargetRefDto>mapToObj(_ -> random.get()).toList();
        var dto = new CommandItemDto(name, targets);
        assertThatThrownBy(() -> fixture.resolveItem(gameState, dto, actor)).isInstanceOf(ConflictException.class);
    }

}
