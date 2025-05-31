package io.github.naomimyselfandi.xanaduwars.gameplay;

import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.gameplay.value.Name;
import io.github.naomimyselfandi.xanaduwars.gameplay.value.Tagged;
import io.github.naomimyselfandi.xanaduwars.testing.SeededRng;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(SeededRandomExtension.class)
class TypeMapperTest {

    private record Definition<T, U>(
            Class<? extends T> type,
            Function<T, U> getter,
            BiFunction<TypeMapper, T, U> method,
            Function<SeededRng, U> factory
    ) {

        static <T extends Tagged> Definition<T, Name> of(
                Class<? extends T> type,
                BiFunction<TypeMapper, T, Name> method
        ) {
            return new Definition<>(type, Tagged::name, method, SeededRng::nextName);
        }

    }

    @RequiredArgsConstructor
    private enum TestCase {

        TYPE(Definition.<Type>of(UnitType.class, TypeMapper::toName)),

        NODE(Definition.<NodeType>of(UnitType.class, TypeMapper::toName)),

        TILE(Definition.of(TileType.class, TypeMapper::toName)),

        TILE_ID(new Definition<>(TileType.class, TileType::id, TypeMapper::toId, SeededRng::nextTileTypeId)),

        UNIT(Definition.of(UnitType.class, TypeMapper::toName)),

        UNIT_ID(new Definition<>(UnitType.class, UnitType::id, TypeMapper::toId, SeededRng::nextUnitTypeId)),

        COMMANDER(Definition.of(Commander.class, TypeMapper::toName)),

        COMMANDER_ID(new Definition<>(Commander.class, Commander::id, TypeMapper::toId, SeededRng::nextCommanderId)),

        SPELL(Definition.of(SpellType.class, TypeMapper::toName)),

        SPELL_ID(new Definition<>(SpellType.class, SpellType::id, TypeMapper::toId, SeededRng::nextSpellTypeId)),

        ACTION(new Definition<>(Action.class, Action::name, TypeMapper::toName, SeededRng::nextName));

        private final Definition<?, ?> definition;

    }

    private final TypeMapper fixture = new TypeMapper() {};

    @EnumSource
    @ParameterizedTest
    void toName(TestCase testCase, SeededRng random) {
        toName(testCase.definition, random);
    }

    private <T, U> void toName(Definition<T, U> definition, SeededRng random) {
        var instance = mock(definition.type);
        var value = definition.factory.apply(random);
        when(definition.getter.apply(instance)).thenReturn(value);
        assertThat(definition.method.apply(fixture, instance)).isEqualTo(value);
    }

    @Test
    void toName_OptionalTileType(SeededRng random) {
        var tileType = mock(TileType.class);
        var name = random.nextName();
        when(tileType.name()).thenReturn(name);
        assertThat(fixture.toName(Optional.of(tileType))).isEqualTo(name);
        assertThat(fixture.toName(Optional.empty())).isNull();
    }

}
