package io.github.naomimyselfandi.xanaduwars.gameplay.runtime;

import io.github.naomimyselfandi.xanaduwars.gameplay.common.CommanderId;
import io.github.naomimyselfandi.xanaduwars.gameplay.common.Resource;
import io.github.naomimyselfandi.xanaduwars.gameplay.common.SpellTypeId;
import io.github.naomimyselfandi.xanaduwars.testing.SeededRng;
import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.gameplay.*;
import io.github.naomimyselfandi.xanaduwars.gameplay.runtime.data.PlayerData;
import io.github.naomimyselfandi.xanaduwars.gameplay.common.PlayerId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
    void setup(SeededRng random) {
        lenient().when(gameState.ruleset()).thenReturn(ruleset);
        var resources = new EnumMap<Resource, Integer>(Resource.class);
        for (var resource : Resource.values()) {
            resources.put(resource, random.nextIntNotNegative());
        }
        var commanderIndex = new CommanderId(random.nextInt(8));
        var commanders = IntStream
                .range(0, 8)
                .mapToObj(index -> index == commanderIndex.index() ? commander : mock())
                .toList();
        when(ruleset.commanders()).thenReturn(commanders);
        playerData = new PlayerData()
                .playerId(new PlayerId(random.nextIntNotNegative()))
                .team(random.nextIntNotNegative())
                .resources(resources)
                .commander(commanderIndex);
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
    void resources(SeededRng random) {
        var resource = random.pick(Resource.values());
        var quantity = random.nextIntNotNegative();
        assertThat(fixture.resources()).isEqualTo(playerData.resources());
        fixture.resource(resource, quantity);
        assertThat(fixture.resources()).isEqualTo(playerData.resources());
        var resources = new EnumMap<>(playerData.resources());
        resources.put(resource, quantity);
        assertThat(playerData.resources()).isEqualTo(resources);
    }

    @RepeatedTest(3)
    void knownSpells(SeededRng random) {
        var spellTypes = createSpellTypes();
        var spellIndex0 = new SpellTypeId(random.nextInt(spellTypes.size()));
        var spellIndex1 = new SpellTypeId(random.nextInt(spellTypes.size()));
        var spellIndex2 = new SpellTypeId(random.nextInt(spellTypes.size()));
        playerData.knownSpells(List.of(spellIndex0, spellIndex1, spellIndex2));
        assertThat(fixture.knownSpells()).containsExactly(
                spellTypes.get(spellIndex0.index()),
                spellTypes.get(spellIndex1.index()),
                spellTypes.get(spellIndex2.index())
        );
    }

    @RepeatedTest(3)
    void activeSpells(SeededRng random) {
        var spellTypes = createSpellTypes();
        var spellTypeId0 = new SpellTypeId(random.nextInt(spellTypes.size()));
        when(spellTypes.get(spellTypeId0.index()).id()).thenReturn(spellTypeId0);
        var spellTypeId1 = new SpellTypeId(random.nextInt(spellTypes.size()));
        when(spellTypes.get(spellTypeId1.index()).id()).thenReturn(spellTypeId1);
        var spellTypeId2 = new SpellTypeId(random.nextInt(spellTypes.size()));
        when(spellTypes.get(spellTypeId2.index()).id()).thenReturn(spellTypeId2);
        assertThat(fixture.activeSpells()).isEmpty();
        fixture.addActiveSpell(spellTypes.get(spellTypeId0.index()));
        assertThat(playerData.activeSpells()).containsExactly(spellTypeId0);
        assertThat(fixture.activeSpells()).containsExactly(
                new SpellImpl(gameState, spellTypes.get(spellTypeId0.index()), fixture, 0)
        );
        fixture.addActiveSpell(spellTypes.get(spellTypeId1.index()));
        assertThat(playerData.activeSpells()).containsExactly(spellTypeId0, spellTypeId1);
        assertThat(fixture.activeSpells()).containsExactly(
                new SpellImpl(gameState, spellTypes.get(spellTypeId0.index()), fixture, 0),
                new SpellImpl(gameState, spellTypes.get(spellTypeId1.index()), fixture, 1)
        );
        fixture.addActiveSpell(spellTypes.get(spellTypeId2.index()));
        assertThat(playerData.activeSpells()).containsExactly(spellTypeId0, spellTypeId1, spellTypeId2);
        assertThat(fixture.activeSpells()).containsExactly(
                new SpellImpl(gameState, spellTypes.get(spellTypeId0.index()), fixture, 0),
                new SpellImpl(gameState, spellTypes.get(spellTypeId1.index()), fixture, 1),
                new SpellImpl(gameState, spellTypes.get(spellTypeId2.index()), fixture, 2)
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
