package io.github.naomimyselfandi.xanaduwars.core.ruleset.service;

import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.core.common.*;
import io.github.naomimyselfandi.xanaduwars.core.ruleset.*;
import io.github.naomimyselfandi.xanaduwars.testing.SeededRng;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.IntFunction;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith({MockitoExtension.class, SeededRandomExtension.class})
@MockitoSettings(strictness = Strictness.LENIENT) // Seriously, Mockito, get over yourself
class TypeNameAndIdMapperTest {

    @Mock
    private Ruleset ruleset;

    @Mock
    private RulesetService rulesetService;

    @Mock
    private VersionService versionService;

    @InjectMocks
    private TypeNameAndIdMapper fixture;

    private SeededRng random;

    @BeforeEach
    void setup(SeededRng random) {
        var version = random.<Version>get();
        when(versionService.published()).thenReturn(List.of(version, random.get()));
        when(rulesetService.load(version)).thenReturn(ruleset);
        this.random = random;
    }

    @Test
    void toCommanderName() {
        toName(TypeNameAndIdMapper::toCommanderName, Ruleset::getCommander, CommanderId::new);
    }

    @Test
    void toSpellName() {
        toName(TypeNameAndIdMapper::toSpellName, Ruleset::getSpell, SpellId::new);
    }

    @Test
    void toStructureTypeName() {
        toName(TypeNameAndIdMapper::toStructureTypeName, Ruleset::getStructureType, StructureTypeId::new);
    }

    @Test
    void toTileTypeName() {
        toName(TypeNameAndIdMapper::toTileTypeName, Ruleset::getTileType, TileTypeId::new);
    }

    @Test
    void toUnitTypeName() {
        toName(TypeNameAndIdMapper::toUnitTypeName, Ruleset::getUnitType, UnitTypeId::new);
    }

    @ParameterizedTest
    @CsvSource(textBlock = """
            Foo,0
            Bar,1
            Baz,2
            SomethingElse,0
            """)
    void toCommanderId(String name, int expected) {
        toId(Ruleset::getCommanders, Commander::getId, CommanderId::new);
        assertThat(fixture.toCommanderId(new Name(name))).isEqualTo(new CommanderId(expected));
    }

    @ParameterizedTest
    @CsvSource(textBlock = """
            Foo,0
            Bar,1
            Baz,2
            SomethingElse,0
            """)
    void toSpellId(String name, int expected) {
        toId(Ruleset::getSpells, Spell::getId, SpellId::new);
        assertThat(fixture.toSpellId(new Name(name))).isEqualTo(new SpellId(expected));
    }

    @ParameterizedTest
    @CsvSource(textBlock = """
            Foo,0
            Bar,1
            Baz,2
            SomethingElse,0
            """)
    void toStructureTypeId(String name, int expected) {
        toId(Ruleset::getStructureTypes, StructureType::getId, StructureTypeId::new);
        assertThat(fixture.toStructureTypeId(new Name(name))).isEqualTo(new StructureTypeId(expected));
    }

    @ParameterizedTest
    @CsvSource(textBlock = """
            Foo,0
            Bar,1
            Baz,2
            SomethingElse,0
            """)
    void toTileTypeId(String name, int expected) {
        toId(Ruleset::getTileTypes, TileType::getId, TileTypeId::new);
        assertThat(fixture.toTileTypeId(new Name(name))).isEqualTo(new TileTypeId(expected));
    }

    @ParameterizedTest
    @CsvSource(textBlock = """
            Foo,0
            Bar,1
            Baz,2
            SomethingElse,0
            """)
    void toUnitTypeId(String name, int expected) {
        toId(Ruleset::getUnitTypes, UnitType::getId, UnitTypeId::new);
        assertThat(fixture.toUnitTypeId(new Name(name))).isEqualTo(new UnitTypeId(expected));
    }

    @SafeVarargs
    private <T extends Declaration, I> void toName(
            BiFunction<TypeNameAndIdMapper, I, Name> target,
            BiFunction<Ruleset, I, T> getter,
            IntFunction<I> idFactory,
            T... reified
    ) {
        var declaration = mock(reified);
        var name = random.<Name>get();
        when(declaration.getName()).thenReturn(name);
        var id = idFactory.apply(random.nextInt());
        when(getter.apply(ruleset, id)).thenReturn(declaration);
        assertThat(target.apply(fixture, id)).isEqualTo(name);
    }

    @SafeVarargs
    private <T extends Declaration, I> void toId(
            Function<Ruleset, List<T>> getter,
            Function<T, I> idGetter,
            IntFunction<I> idFactory,
            T... reified
    ) {
        var foo = mock(reified);
        when(foo.getName()).thenReturn(new Name("Foo"));
        when(idGetter.apply(foo)).thenReturn(idFactory.apply(0));
        var bar = mock(reified);
        when(bar.getName()).thenReturn(new Name("Bar"));
        when(idGetter.apply(bar)).thenReturn(idFactory.apply(1));
        var baz = mock(reified);
        when(baz.getName()).thenReturn(new Name("Baz"));
        when(idGetter.apply(baz)).thenReturn(idFactory.apply(2));
        when(getter.apply(ruleset)).thenReturn(List.of(foo, bar, baz));
    }

}
