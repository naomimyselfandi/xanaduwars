package io.github.naomimyselfandi.xanaduwars.core.runtime;

import io.github.naomimyselfandi.seededrandom.SeededRandom;
import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.core.*;
import io.github.naomimyselfandi.xanaduwars.core.data.PlayerData;
import io.github.naomimyselfandi.xanaduwars.core.queries.VisionQuery;
import io.github.naomimyselfandi.xanaduwars.core.wrapper.PlayerId;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.EnumMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith({MockitoExtension.class, SeededRandomExtension.class})
class PlayerImplTest {

    @Mock
    private AugmentedGameState gameState;

    @Mock
    private Ruleset ruleset;

    @Mock
    private Commander commander;

    private PlayerData playerData;

    private PlayerImpl fixture;

    @BeforeEach
    void setup(SeededRandom random) {
        lenient().when(gameState.ruleset()).thenReturn(ruleset);
        var resources = new EnumMap<Resource, Integer>(Resource.class);
        for (var resource : Resource.values()) {
            resources.put(resource, random.nextInt(Integer.MAX_VALUE));
        }
        var commanderIndex = random.nextInt(8);
        var commanders = IntStream
                .range(0, 8)
                .mapToObj(index -> index == commanderIndex ? commander : mock())
                .toList();
        when(ruleset.commanders()).thenReturn(commanders);
        playerData = new PlayerData()
                .playerId(new PlayerId(random.nextInt(Integer.MAX_VALUE)))
                .team(random.nextInt(Integer.MAX_VALUE))
                .resources(resources)
                .playerType(commanderIndex);
        fixture = new PlayerImpl(gameState, playerData);
    }

    @Test
    void team() {
        assertThat(fixture.team()).isEqualTo(playerData.team());
    }

    @Test
    void defeated() {
        assertThat(fixture.defeated()).isFalse();
        fixture.defeat();
        assertThat(playerData.defeated()).isTrue();
        assertThat(fixture.defeated()).isTrue();
    }

    @ParameterizedTest
    @CsvSource(textBlock = """
            UNIT,true
            UNIT,false
            TILE,true
            TILE,false
            """)
    void canSee(String kind, boolean expected) {
        var node = switch (kind) {
            case "UNIT" -> mock(Unit.class);
            case "TILE" -> mock(Tile.class);
            default -> Assertions.<Node>fail();
        };
        when(gameState.evaluate(new VisionQuery(fixture, node))).thenReturn(expected);
        assertThat(fixture.canSee(node)).isEqualTo(expected);
    }

    @Test
    void units() {
        var anotherPlayer = mock(Player.class);
        var units = IntStream.range(0, 4).mapToObj(i -> {
            var unit = mock(Unit.class);
            when(unit.owner()).thenReturn(Optional.ofNullable(switch (i) {
                case 0, 3 -> fixture;
                case 1 -> anotherPlayer;
                default -> null;
            }));
            return unit;
        }).toList();
        when(gameState.units()).thenReturn(units);
        assertThat(fixture.units()).containsExactly(units.getFirst(), units.getLast());
    }

    @Test
    void tiles() {
        var anotherPlayer = mock(Player.class);
        var tiles = IntStream
                .range(0, 4)
                .mapToObj(y -> IntStream
                        .range(0, 4)
                        .mapToObj(x -> {
                            var tile = mock(Tile.class);
                            if (x == y) {
                                var player = (x == 0) || (x == 3) ? fixture : anotherPlayer;
                                when(tile.owner()).thenReturn(Optional.of(player));
                            }
                            return tile;
                        })
                        .toList())
                .toList();
        when(gameState.tiles()).thenReturn(tiles);
        assertThat(fixture.tiles()).containsExactly(tiles.getFirst().getFirst(), tiles.getLast().getLast());
    }

    @Test
    void resources(SeededRandom random) {
        var resource = random.pick(Resource.values());
        var quantity = random.nextInt(Integer.MAX_VALUE);
        assertThat(fixture.resources()).isEqualTo(playerData.resources());
        fixture.resource(resource, quantity);
        assertThat(fixture.resources()).isEqualTo(playerData.resources());
        var resources = new EnumMap<>(playerData.resources());
        resources.put(resource, quantity);
        assertThat(playerData.resources()).isEqualTo(resources);
    }

    @RepeatedTest(3)
    void knownSpells(SeededRandom random) {
        var spellTypes = createSpellTypes();
        var spellIndex0 = random.nextInt(spellTypes.size());
        var spellIndex1 = random.nextInt(spellTypes.size());
        var spellIndex2 = random.nextInt(spellTypes.size());
        playerData.knownSpells(List.of(spellIndex0, spellIndex1, spellIndex2));
        assertThat(fixture.knownSpells()).containsExactly(
                spellTypes.get(spellIndex0),
                spellTypes.get(spellIndex1),
                spellTypes.get(spellIndex2)
        );
    }

    @RepeatedTest(3)
    void activeSpells(SeededRandom random) {
        var spellTypes = createSpellTypes();
        var spellIndex0 = random.nextInt(spellTypes.size());
        when(spellTypes.get(spellIndex0).index()).thenReturn(spellIndex0);
        var spellIndex1 = random.nextInt(spellTypes.size());
        when(spellTypes.get(spellIndex1).index()).thenReturn(spellIndex1);
        var spellIndex2 = random.nextInt(spellTypes.size());
        when(spellTypes.get(spellIndex2).index()).thenReturn(spellIndex2);
        assertThat(fixture.activeSpells()).isEmpty();
        fixture.addActiveSpell(spellTypes.get(spellIndex0));
        assertThat(playerData.activeSpells()).containsExactly(spellIndex0);
        assertThat(fixture.activeSpells()).containsExactly(
                new SpellImpl(gameState, spellTypes.get(spellIndex0), fixture, 0)
        );
        fixture.addActiveSpell(spellTypes.get(spellIndex1));
        assertThat(playerData.activeSpells()).containsExactly(spellIndex0, spellIndex1);
        assertThat(fixture.activeSpells()).containsExactly(
                new SpellImpl(gameState, spellTypes.get(spellIndex0), fixture, 0),
                new SpellImpl(gameState, spellTypes.get(spellIndex1), fixture, 1)
        );
        fixture.addActiveSpell(spellTypes.get(spellIndex2));
        assertThat(playerData.activeSpells()).containsExactly(spellIndex0, spellIndex1, spellIndex2);
        assertThat(fixture.activeSpells()).containsExactly(
                new SpellImpl(gameState, spellTypes.get(spellIndex0), fixture, 0),
                new SpellImpl(gameState, spellTypes.get(spellIndex1), fixture, 1),
                new SpellImpl(gameState, spellTypes.get(spellIndex2), fixture, 2)
        );
        fixture.clearActiveSpells();
        assertThat(playerData.activeSpells()).isEmpty();
        assertThat(fixture.activeSpells()).isEmpty();
    }

    @Test
    void owner() {
        assertThat(fixture.owner()).contains(fixture);
    }

    @Test
    void type() {
        assertThat(fixture.type()).isEqualTo(commander);
    }

    private List<SpellType<?>> createSpellTypes() {
        var spellTypes = IntStream.range(0, 20).<SpellType<?>>mapToObj(_ -> mock()).toList();
        when(ruleset.spellTypes()).thenReturn(spellTypes);
        return spellTypes;
    }

}
