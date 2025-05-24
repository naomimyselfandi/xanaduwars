package io.github.naomimyselfandi.xanaduwars.core.runtime;

import io.github.naomimyselfandi.seededrandom.SeededRandom;
import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.core.Player;
import io.github.naomimyselfandi.xanaduwars.core.SpellType;
import io.github.naomimyselfandi.xanaduwars.core.queries.TagsQuery;
import io.github.naomimyselfandi.xanaduwars.core.wrapper.Name;
import io.github.naomimyselfandi.xanaduwars.core.wrapper.PlayerId;
import io.github.naomimyselfandi.xanaduwars.core.wrapper.Tag;
import io.github.naomimyselfandi.xanaduwars.core.wrapper.TagSet;
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

    private int index;

    private SpellImpl fixture;

    @BeforeEach
    void setup(SeededRandom random) {
        when(owner.id()).thenReturn(new PlayerId(random.nextInt(Integer.MAX_VALUE)));
        index = random.nextInt(Integer.MAX_VALUE);
        fixture = new SpellImpl(gameState, type, owner, index);
    }

    @Test
    void owner() {
        assertThat(fixture.owner()).contains(owner);
    }

    @Test
    void name(SeededRandom random) {
        var name = new Name("S" + random.nextInt(Integer.MAX_VALUE));
        when(type.name()).thenReturn(name);
        assertThat(fixture.name()).isEqualTo(name);
    }

    @Test
    void tags(SeededRandom random) {
        var foo = new Tag("F" + random.nextInt(Integer.MAX_VALUE));
        when(type.tags()).thenReturn(TagSet.of(foo));
        var bar = new Tag("B" + random.nextInt(Integer.MAX_VALUE));
        when(gameState.evaluate(new TagsQuery(fixture), TagSet.of(foo))).thenReturn(TagSet.of(bar));
        assertThatCollection(fixture.tags()).containsOnly(bar);
    }

    @Test
    void testToString(SeededRandom random) {
        var name = new Name("S" + random.nextInt(Integer.MAX_VALUE));
        when(type.name()).thenReturn(name);
        assertThat(fixture).hasToString("Spell(%d, %d)[%s]", owner.id().intValue(), index, name);
    }

}
