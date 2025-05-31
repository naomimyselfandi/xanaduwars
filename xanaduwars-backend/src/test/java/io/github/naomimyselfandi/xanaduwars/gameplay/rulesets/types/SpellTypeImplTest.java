package io.github.naomimyselfandi.xanaduwars.gameplay.rulesets.types;

import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.gameplay.GameState;
import io.github.naomimyselfandi.xanaduwars.gameplay.Player;
import io.github.naomimyselfandi.xanaduwars.gameplay.value.Resource;
import io.github.naomimyselfandi.xanaduwars.gameplay.value.SpellTypeId;
import io.github.naomimyselfandi.xanaduwars.testing.SeededRng;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith({MockitoExtension.class, SeededRandomExtension.class})
class SpellTypeImplTest {

    @Mock
    private Player player;

    @Mock
    private Object target;

    @Mock
    private BiConsumer<Player, Object> consumer;

    private final class TestSpellType extends SpellTypeImpl<Object> {

        @Override
        void onCast(Player user, Object target) {
           consumer.accept(user, target);
        }

        @Override
        public Stream<Object> enumerateTargets(GameState gameState) {
            throw new UnsupportedOperationException();
        }

    }

    @Test
    void initialize(SeededRng random) {
        var fooName = random.nextName().toString();
        var foo = new TestSpellType();
        var barName = random.nextName().toString();
        var bar = new TestSpellType();
        var bazName = random.nextName().toString();
        var baz = new TestSpellType();
        SpellTypeImpl.initialize(List.of(foo, bar, baz), List.of(fooName, barName, bazName));
        assertThat(foo.name()).hasToString(fooName);
        assertThat(foo.id()).isEqualTo(new SpellTypeId(0));
        assertThat(bar.name()).hasToString(barName);
        assertThat(bar.id()).isEqualTo(new SpellTypeId(1));
        assertThat(baz.name()).hasToString(bazName);
        assertThat(baz.id()).isEqualTo(new SpellTypeId(2));
    }

    @ParameterizedTest
    @CsvSource(textBlock = """
            100,0,SUPPLIES
            100,0,AETHER,
            100,100,FOCUS
            200,200,FOCUS
            """)
    void cost(int focusCost, int expected, Resource resource) {
        var instance = new TestSpellType().focusCost(focusCost);
        assertThat(instance.cost(resource, player, target)).isEqualTo(expected);
        verifyNoInteractions(player, target);
    }

    @Test
    void execute() {
        var instance = new TestSpellType();
        instance.execute(player, target);
        var inOrder = inOrder(player, consumer);
        inOrder.verify(player).addActiveSpell(instance);
        inOrder.verify(consumer).accept(player, target);
    }

    @Test
    void testToString(SeededRng random) {
        var id = random.nextSpellTypeId();
        var name = random.nextName();
        var instance = new TestSpellType().id(id).name(name);
        assertThat(instance).hasToString("%s(%s)", id, name);
    }

}
