package io.github.naomimyselfandi.xanaduwars.core.gamestate.service.command;

import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.core.gamestate.GameState;
import io.github.naomimyselfandi.xanaduwars.core.gamestate.Unit;
import io.github.naomimyselfandi.xanaduwars.core.gamestate.dto.CommandDto;
import io.github.naomimyselfandi.xanaduwars.core.gamestate.dto.CommandItemDto;
import io.github.naomimyselfandi.xanaduwars.core.gamestate.dto.PhysicalRefDto;
import io.github.naomimyselfandi.xanaduwars.testing.SeededRng;
import io.github.naomimyselfandi.xanaduwars.util.ConflictException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith({MockitoExtension.class, SeededRandomExtension.class})
class CommandResolverImplTest {

    private PhysicalRefDto actorDto;

    private CommandItemDto itemDto0, itemDto1;

    private Command.Item item0, item1;

    private CommandDto commandDto;

    @Mock
    private GameState gameState;

    @Mock
    private Unit actor;

    @Mock
    private ObjectResolver objectResolver;

    @Mock
    private ItemResolver itemResolver;

    @InjectMocks
    private CommandResolverImpl fixture;

    @BeforeEach
    void setup(SeededRng random) {
        actorDto = random.get();
        itemDto0 = random.get();
        itemDto1 = random.get();
        item0 = random.get();
        item1 = random.get();
        commandDto = new CommandDto(actorDto, List.of(itemDto0, itemDto1));
    }

    @Test
    void resolve() throws ConflictException {
        when(objectResolver.resolveActor(gameState, actorDto)).thenReturn(actor);
        when(itemResolver.resolveItem(gameState, itemDto0, actor)).thenReturn(item0);
        when(itemResolver.resolveItem(gameState, itemDto1, actor)).thenReturn(item1);
        assertThat(fixture.resolve(gameState, commandDto)).isEqualTo(new Command(actor, List.of(item0, item1)));
    }

}
