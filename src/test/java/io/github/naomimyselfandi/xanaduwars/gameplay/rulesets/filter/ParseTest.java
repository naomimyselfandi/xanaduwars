package io.github.naomimyselfandi.xanaduwars.gameplay.rulesets.filter;

import io.github.naomimyselfandi.xanaduwars.gameplay.*;
import io.github.naomimyselfandi.xanaduwars.gameplay.common.Name;
import io.github.naomimyselfandi.xanaduwars.gameplay.common.Tag;
import io.github.naomimyselfandi.xanaduwars.gameplay.common.TagSet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ParseTest {

    @Mock
    private Ruleset ruleset;

    @BeforeEach
    void setup() {
        var commanders = types(Commander.class);
        var tileTypes = types(TileType.class);
        var unitTypes = types(UnitType.class);
        lenient().when(ruleset.commanders()).thenReturn(commanders);
        lenient().when(ruleset.tileTypes()).thenReturn(tileTypes);
        lenient().when(ruleset.unitTypes()).thenReturn(unitTypes);
    }

    @ParameterizedTest
    @CsvSource(textBlock = """
            Object:Object:*
            Object:Object:!*
            Element:Player:name.Commander0
            Element:Player:tag.CommanderTag0
            Element:Player:all(tag.CommanderTag0, tag.CommanderTag1)
            Element:Player:any(name.Commander0, name.Commander1)
            Element:Player:!name.Commander0
            Element:Player:!tag.CommanderTag0
            Element:Tile:name.TileType0
            Element:Tile:tag.TileTag0
            Element:Tile:all(tag.TileTag0, tag.TileTag1)
            Element:Tile:any(name.TileType0, name.TileType1)
            Element:Tile:!name.TileType0
            Element:Tile:!tag.TileTag0
            Element:Unit:name.UnitType0
            Element:Unit:tag.UnitTag0
            Element:Unit:all(tag.UnitTag0, tag.UnitTag1)
            Element:Unit:any(name.UnitType0, name.UnitType1)
            Element:Unit:!name.UnitType0
            Element:Unit:!tag.UnitTag0
            Element:Node:name.UnitType0
            Element:Node:name.TileType0
            Element:Element:name.UnitType0
            Element:Element:name.TileType0
            Node:Node:range(1-2)
            Unit:Tile:range(1-2)
            Tile:Unit:range(1-2)
            Element:Element:owner.name.Commander0
            Element:Element:as(Unit).name.UnitType0
            Element:Element:own
            Element:Element:ally
            Element:Element:enemy
            Element:Tile:unit.owner.spell.*
            Element:Unit:tile.owner.spell.*
            Unit:Node:range(attack)
            Unit:Node:range(vision)
            Unit:Node:hasDamageValue
            Node:Unit:@hasDamageValue
            Node:Unit:@range(attack)
            """, delimiter = ':')
    void read(String subject, String target, String text) {
        var s = javaClass(subject);
        var t = javaClass(target);
        assertThat(new Parse(ruleset, text).read(s, t)).hasToString(text);
    }

    @ParameterizedTest
    @CsvSource(textBlock = """
            Player:Unit:At position 0, 'unit' doesn't look like a BiFilter<Player, Unit>.:unit.*
            Player:Tile:At position 0, 'tile' doesn't look like a BiFilter<Player, Tile>.:tile.*
            Unit:Unit:At position 10, ')' doesn't look like a BiFilter<Unit, Unit>.:all(tag.UnitTag0, tag.UnitTag1, )
            Unit:Unit:At position 0, 'spell' doesn't look like a BiFilter<Unit, Unit>.:spell.*
            Unit:Unit:Unexpected end of input.:all(tag.UnitTag0, tag.UnitTag1,
            Unit:Unit:At position 9, expected ')' or ',' but got the end of the input.:all(tag.UnitTag0, tag.UnitTag1
            Unit:Unit:Extraneous input starting at position 1 ('@').:*@!$
            Unit:Unit:Unknown Unit name 'UnitTag0'.:name.UnitTag0
            Unit:Unit:Unknown Unit tag 'UnitType0'.:tag.UnitType0
            Tile:Tile:Unknown Tile name 'TileTag0'.:name.TileTag0
            Tile:Tile:Unknown Tile tag 'TileType0'.:tag.TileType0
            Player:Player:Unknown Player name 'PlayerTag0'.:name.PlayerTag0
            Player:Player:Unknown Player tag 'Commander0'.:tag.Commander0
            Node:Node:Unknown Node tag 'PlayerTag0'.:tag.PlayerTag0
            Unit:Unit:At position 2, 'foo' doesn't look like a BiFilter<Unit, Player>.:owner.foo
            Unit:Object:At position 0, 'own' doesn't look like a BiFilter<Unit, Object>.:own
            Object:Unit:At position 0, 'own' doesn't look like a BiFilter<Object, Unit>.:own
            Unit:Object:At position 0, 'name' doesn't look like a BiFilter<Unit, Object>.:name.Foo
            Unit:Object:At position 0, 'tag' doesn't look like a BiFilter<Unit, Object>.:tag.Bar
            Tile:Unit:At position 2, expected an integer but got 'attack'.:range(attack)
            Tile:Unit:At position 2, expected an integer but got 'vision'.:range(vision)
            """, delimiter = ':')
    void read_Exceptions(String subject, String target, String message, String text) {
        var s = javaClass(subject);
        var t = javaClass(target);
        assertThatThrownBy(() -> new Parse(ruleset, text).read(s, t)).hasMessage(message);
    }

    @ParameterizedTest
    @CsvSource(textBlock = """
            Unit:Unit:name.Foo
            Unit:Unit:tag.Bar
            """, delimiter = ':')
    void read_ToleratesMissingRuleset(String subject, String target, String text) {
        var s = javaClass(subject);
        var t = javaClass(target);
        assertThat(new Parse(null, text).read(s, t)).hasToString(text);
    }

    private static Class<?> javaClass(String name) {
        return name.equals("Object") ? Object.class : Classes.ELEMENT_TYPES_BY_NAME.get(name);
    }

    private <T extends Type> List<T> types(Class<T> type) {
        var simpleName = type.getSimpleName();
        return IntStream
                .rangeClosed(0, 1)
                .mapToObj(index -> {
                    var name = new Name(simpleName + index);
                    var tag = new Tag(simpleName.replaceAll("Type", "") + "Tag" + index);
                    var result = mock(type);
                    lenient().when(result.name()).thenReturn(name);
                    lenient().when(result.tags()).thenReturn(TagSet.of(tag));
                    return result;
                })
                .toList();
    }

}
