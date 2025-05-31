package io.github.naomimyselfandi.xanaduwars.gameplay.rulesets.rules;

import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.gameplay.*;
import io.github.naomimyselfandi.xanaduwars.gameplay.queries.ActionCleanupEvent;
import io.github.naomimyselfandi.xanaduwars.ext.None;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith({MockitoExtension.class, SeededRandomExtension.class})
class PostActionRuleTest {

    @Mock
    private Unit unit;

    @Mock
    private Tile tile;

    @Mock
    private Player player;

    @Mock
    private Spell spell;

    @InjectMocks
    private PostActionRule fixture;

    @CsvSource(textBlock = """
            UNIT,true
            TILE,false
            PLAYER,false
            SPELL,false
            """)
    @ParameterizedTest
    void handles(String kind, boolean expected) {
        var subject = switch (kind) {
            case "UNIT" -> unit;
            case "TILE" -> tile;
            case "PLAYER" -> player;
            case "SPELL" -> spell;
            default -> Assertions.<Element>fail();
        };
        assertThat(fixture.handles(new ActionCleanupEvent(subject), None.NONE)).isEqualTo(expected);
    }

    @Test
    void handle() {
        assertThat(fixture.handle(new ActionCleanupEvent(unit), None.NONE)).isEqualTo(None.NONE);
        verify(unit).canAct(false);
    }

}
