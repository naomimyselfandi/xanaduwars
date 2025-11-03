package io.github.naomimyselfandi.xanaduwars.core.impl;

import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.core.model.*;
import io.github.naomimyselfandi.xanaduwars.core.script.Script;
import io.github.naomimyselfandi.xanaduwars.testing.SeededRng;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@MockitoSettings(strictness = Strictness.LENIENT)
@ExtendWith({MockitoExtension.class, SeededRandomExtension.class})
class CopyMachineImplTest {

    @Mock
    private Version version;

    @Mock
    private Script redactionPolicy;

    @Mock
    private Player player0, player1, player2, player3;

    @Mock
    private Tile tile00, tile10, tile20, tile01, tile11, tile21;

    @Mock
    private Unit unit0, unit1, unit2, unit3, unit4;

    @Mock
    private GameState source;

    private CopyMachineImpl fixture;

    private SeededRng random;

    @BeforeEach
    void setup(SeededRng random) {
        this.random = random;
        fixture = new CopyMachineImpl();
        when(source.getVersion()).thenReturn(version);
        when(source.getWidth()).thenReturn(3);
        when(source.getHeight()).thenReturn(2);
        var players = List.of(player0, player1, player2, player3);
        when(source.getPlayers()).thenReturn(players);
        initPlayers(players);
        var tiles = List.of(tile00, tile10, tile20, tile01, tile11, tile21);
        when(source.getTiles()).thenReturn(tiles);
        initTiles(tiles, 3, 2);
        initUnit(player0, tile00, unit0, unit1);
        initUnit(player1, tile20, unit2, unit3);
        initUnit(player2, tile11, unit4);
        when(source.getTurn()).thenReturn(random.nextInt());
        when(source.isRedacted()).thenReturn(random.nextBoolean());
        lenient().when(version.getRedactionPolicy()).thenReturn(redactionPolicy);
    }

    @RepeatedTest(4)
    void createCopy() {
        var copy = fixture.createCopy(source);
        assertConsistent(copy);
        assertThat(copy.getVersion()).isEqualTo(version);
        assertThat(copy.getTurn()).isEqualTo(source.getTurn());
        assertThat(copy.isRedacted()).isEqualTo(source.isRedacted());
        assertThat(copy.getWidth()).isEqualTo(source.getWidth());
        assertThat(copy.getHeight()).isEqualTo(source.getHeight());
        assertThat(copy.getTiles()).hasSize(6);
        assertThat(copy.getPlayers()).hasSize(4);
        assertCopy(copy.getPlayer(0), player0);
        assertCopy(copy.getPlayer(1), player1);
        assertCopy(copy.getPlayer(2), player2);
        assertCopy(copy.getPlayer(3), player3);
        assertCopy(copy.getTile(0, 0), tile00);
        assertCopy(copy.getTile(1, 0), tile10);
        assertCopy(copy.getTile(2, 0), tile20);
        assertCopy(copy.getTile(0, 1), tile01);
        assertCopy(copy.getTile(1, 1), tile11);
        assertCopy(copy.getTile(2, 1), tile21);
        verifyNoInteractions(redactionPolicy);
    }

    @RepeatedTest(4)
    void createRedactedCopy(SeededRng random) {
        var player = random.pick(player0, player1, player2, player3);
        when(player.isEnemy(player0)).thenReturn(false);
        when(player.isEnemy(player1)).thenReturn(true);
        when(player.isEnemy(player2)).thenReturn(false);
        when(player.isEnemy(player3)).thenReturn(true);
        when(player.perceives(unit0)).thenReturn(true);
        when(player.perceives(unit1)).thenReturn(true);
        when(player.perceives(unit2)).thenReturn(true);
        when(player.perceives(unit3)).thenReturn(false);
        when(player.perceives(unit4)).thenReturn(false);
        var copy = fixture.createRedactedCopy(source, player);
        assertConsistent(copy);
        assertThat(copy.getVersion()).isEqualTo(version);
        assertThat(copy.getTurn()).isEqualTo(source.getTurn());
        assertThat(copy.isRedacted()).isEqualTo(true);
        assertThat(copy.getWidth()).isEqualTo(source.getWidth());
        assertThat(copy.getHeight()).isEqualTo(source.getHeight());
        assertThat(copy.getTiles()).hasSize(6);
        assertThat(copy.getPlayers()).hasSize(4);
        assertCopy(copy.getPlayer(0), player0);
        assertCopy(copy.getPlayer(1), player1);
        assertCopy(copy.getPlayer(2), player2);
        assertCopy(copy.getPlayer(3), player3);
        assertCopy(copy.getTile(0, 0), tile00);
        assertCopy(copy.getTile(1, 0), tile10);
        assertCopy(copy.getTile(2, 0), tile20, 1);
        assertCopy(copy.getTile(0, 1), tile01);
        assertCopy(copy.getTile(1, 1), tile11, 0);
        assertCopy(copy.getTile(2, 1), tile21);
        verify(redactionPolicy).execute(copy, Map.of("viewpoint", copy.getPlayer(player.getPosition())));
        verifyNoMoreInteractions(redactionPolicy);
    }

    private void initPlayers(List<Player> players) {
        for (var i = 0; i < players.size(); i++) {
            var player = players.get(i);
            when(player.getPosition()).thenReturn(i);
            when(player.getTeam()).thenReturn(random.nextInt());
            when(player.getSupplies()).thenReturn(random.nextInt());
            when(player.getAether()).thenReturn(random.nextInt());
            when(player.getFocus()).thenReturn(random.nextInt());
            when(player.isDefeated()).thenReturn(random.nextBoolean());
            var commander = mock(Commander.class);
            when(player.getCommander()).thenReturn(commander);
            var abilities = getAbilities();
            when(player.getAbilities()).thenReturn(abilities);
            var activeAbilities = getAbilities();
            when(player.getActiveAbilities()).thenReturn(activeAbilities);
            var usedAbilities = getAbilities();
            when(player.getUsedAbilities()).thenReturn(usedAbilities);
        }
    }

    @SuppressWarnings("SameParameterValue")
    private void initTiles(List<Tile> tiles, int width, int height) {
        for (var y = 0; y < height; y++) {
            for (var x = 0; x < width; x++) {
                var tile = tiles.get(y * 3 + x);
                when(tile.getX()).thenReturn(x);
                when(tile.getY()).thenReturn(y);
                var type = mock(TileType.class);
                when(tile.getType()).thenReturn(type);
            }
        }
    }

    private void initUnit(Player owner, Node location, Unit... units) {
        if (units.length > 0) {
            var head = units[0];
            var tail = Arrays.stream(units).skip(1).toArray(Unit[]::new);
            var type = mock(UnitType.class);
            when(type.getMaxHp()).thenReturn(Integer.MAX_VALUE);
            var activeAbilities = getAbilities();
            when(head.getType()).thenReturn(type);
            when(head.getOwner()).thenReturn(owner);
            when(location.getUnit()).thenReturn(head);
            when(head.getLocation()).thenReturn(location);
            when(head.getHpPercent()).thenReturn(random.nextInt(1, 10000) / 10000.0);
            when(head.getActiveAbilities()).thenReturn(activeAbilities);
            initUnit(owner, head, tail);
        }
    }

    private List<Ability> getAbilities() {
        return IntStream.range(0, random.nextInt(1, 4)).mapToObj(_ -> mock(Ability.class)).toList();
    }

    private static void assertConsistent(GameState gameState) {
        assertThat(gameState.getPlayers()).isUnmodifiable().allSatisfy(it -> {
            assertThat(it.getGameState()).isSameAs(gameState);
            assertThat(gameState.getPlayer(it.getPosition())).isSameAs(it);
            assertThat(gameState.getPlayers().get(it.getPosition())).isSameAs(it);
        });
        assertThat(gameState.getTiles()).isUnmodifiable().allSatisfy(it -> {
            assertThat(it.getGameState()).isSameAs(gameState);
            assertThat(gameState.getTile(it.getX(), it.getY())).isSameAs(it);
        });
        assertThat(gameState.getUnits()).allSatisfy(it -> {
            assertThat(it.getGameState()).isSameAs(gameState);
            assertThat(it.getLocation().getUnit()).isSameAs(it);
            assertThat(it.getOwner().getGameState()).isSameAs(gameState);
        });
        for (var y = 0; y < gameState.getHeight(); y++) {
            for (var x = 0; x < gameState.getWidth(); x++) {
                assertThat(gameState.getTile(x, y)).isSameAs(gameState.getTiles().get(y * gameState.getWidth() + x));
            }
        }
    }

    private static void assertCopy(Player copy, Player source) {
        assertThat(copy.getPosition()).isEqualTo(source.getPosition());
        assertThat(copy.getTeam()).isEqualTo(source.getTeam());
        assertThat(copy.getCommander()).isEqualTo(source.getCommander());
        assertThat(copy.getAbilities()).isEqualTo(source.getAbilities());
        assertThat(copy.getActiveAbilities()).isEqualTo(source.getActiveAbilities());
        assertThat(copy.getUsedAbilities()).isEqualTo(source.getUsedAbilities());
        assertThat(copy.isDefeated()).isEqualTo(source.isDefeated());
        assertThat(copy.getSupplies()).isEqualTo(source.getSupplies());
        assertThat(copy.getAether()).isEqualTo(source.getAether());
        assertThat(copy.getFocus()).isEqualTo(source.getFocus());
    }

    private static void assertCopy(@Nullable Tile copy, Tile source) {
        assertCopy(copy, source, Integer.MAX_VALUE);
    }

    private static void assertCopy(@Nullable Tile copy, Tile source, int units) {
        assertThat(copy).isNotNull();
        assertThat(copy.getX()).isEqualTo(source.getX());
        assertThat(copy.getY()).isEqualTo(source.getY());
        assertThat(copy.getType()).isEqualTo(source.getType());
        assertCopy(copy.getUnit(), source.getUnit(), units);
    }

    private static void assertCopy(@Nullable Unit copy, @Nullable Unit source, int units) {
        if (units == 0 || source == null) {
            assertThat(copy).isNull();
            return;
        }
        assertThat(copy).isNotNull();
        assertThat(copy.getType()).isEqualTo(source.getType());
        assertThat(copy.getOwner().getPosition()).isEqualTo(source.getOwner().getPosition());
        assertThat(copy.getHpPercent()).isEqualTo(source.getHpPercent());
        assertCopy(copy.getUnit(), source.getUnit(), units - 1);
    }

}
