package io.github.naomimyselfandi.xanaduwars.gameplay.runtime;

import io.github.naomimyselfandi.xanaduwars.testing.SeededRng;
import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.gameplay.Player;
import io.github.naomimyselfandi.xanaduwars.gameplay.SpellType;
import io.github.naomimyselfandi.xanaduwars.gameplay.queries.TagsQuery;
import io.github.naomimyselfandi.xanaduwars.gameplay.value.TagSet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith({MockitoExtension.class, SeededRandomExtension.class})
class SpellImplTest {

    @Mock
    private AugmentedGameState gameState;

    @Mock
    private SpellType<?> type;

    @Mock
    private Player owner;


    private SpellImpl fixture;

    @BeforeEach
    void setup(SeededRng random) {
        when(owner.id()).thenReturn(random.nextPlayerId());
        var index = random.nextIntNotNegative();
        fixture = new SpellImpl(gameState, type, owner, index);
    }

    @Test
    void owner() {
        assertThat(fixture.owner()).contains(owner);
    }

    @Test
    void name(SeededRng random) {
        var name = random.nextName();
        when(type.name()).thenReturn(name);
        assertThat(fixture.name()).isEqualTo(name);
    }

    @Test
    void tags(SeededRng random) {
        var tag = random.nextTag();
        when(gameState.evaluate(new TagsQuery(fixture))).thenReturn(TagSet.of(tag));
        assertThatCollection(fixture.tags()).containsOnly(tag);
    }

    @Test
    void testToString(SeededRng random) {
        var name = random.nextName();
        when(type.name()).thenReturn(name);
        assertThat(fixture).hasToString("%s[%s]", fixture.id(), name);
    }

}
