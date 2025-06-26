package io.github.naomimyselfandi.xanaduwars.game.dto;

import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.core.gamestate.PlayerId;
import io.github.naomimyselfandi.xanaduwars.game.entity.Game;
import io.github.naomimyselfandi.xanaduwars.game.entity.PlayerSlot;
import io.github.naomimyselfandi.xanaduwars.testing.SeededRng;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;
import java.util.TreeMap;

import static org.assertj.core.api.Assertions.*;

@ExtendWith({MockitoExtension.class, SeededRandomExtension.class})
class GameDtoConverterTest {

    private PlayerSlot slot0, slot1, slot2;

    private PlayerSlotDto dto0, dto1, dto2;

    private GameDtoConverter fixture;

    @BeforeEach
    void setup(SeededRng random) {
        slot0 = random.get();
        slot1 = random.get();
        slot2 = random.get();
        dto0 = random.<PlayerSlotDto>get().setId(new PlayerId(0));
        dto1 = random.<PlayerSlotDto>get().setId(new PlayerId(1));
        dto2 = random.<PlayerSlotDto>get().setId(new PlayerId(2));
        var map = Map.of(slot0, dto0, slot1, dto1, slot2, dto2);
        fixture = new GameDtoConverter() {

            @Override
            public PlayerSlotDto convert(PlayerSlot source) {
                return map.get(source);
            }

            @Override
            public GameDto convert(Game source) {
                throw new UnsupportedOperationException();
            }

        };
    }

    @Test
    void convert() {
        var source = new TreeMap<PlayerId, PlayerSlot>();
        source.put(new PlayerId(0), slot0);
        source.put(new PlayerId(1), slot1);
        source.put(new PlayerId(2), slot2);
        assertThat(fixture.convert(source)).containsExactly(dto0, dto1, dto2);
    }

}
