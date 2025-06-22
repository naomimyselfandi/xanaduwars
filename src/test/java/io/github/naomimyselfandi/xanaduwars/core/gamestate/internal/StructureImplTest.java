package io.github.naomimyselfandi.xanaduwars.core.gamestate.internal;

import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.core.common.StructureTag;
import io.github.naomimyselfandi.xanaduwars.core.common.StructureTypeId;
import io.github.naomimyselfandi.xanaduwars.core.gamestate.*;
import io.github.naomimyselfandi.xanaduwars.core.gamestate.entity.StructureData;
import io.github.naomimyselfandi.xanaduwars.core.gamestate.queries.StructureDestructionEvent;
import io.github.naomimyselfandi.xanaduwars.core.gamestate.queries.StructureTagQuery;
import io.github.naomimyselfandi.xanaduwars.core.gamestate.queries.VisionRangeQuery;
import io.github.naomimyselfandi.xanaduwars.core.ruleset.NormalAction;
import io.github.naomimyselfandi.xanaduwars.core.ruleset.Ruleset;
import io.github.naomimyselfandi.xanaduwars.core.ruleset.StructureType;
import io.github.naomimyselfandi.xanaduwars.core.scripting.Rule;
import io.github.naomimyselfandi.xanaduwars.testing.SeededRng;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@MockitoSettings(strictness = Strictness.LENIENT)
@ExtendWith({MockitoExtension.class, SeededRandomExtension.class})
class StructureImplTest {

    @Mock
    private Rule rule;

    @Mock
    private NormalAction action0, action1;

    @Mock
    private Player alice, bob, charlie, dave;

    @Mock
    private Tile tile;

    @Mock
    private StructureType type, anotherType;

    @Mock
    private Ruleset ruleset;

    private StructureData data;

    @Mock
    private GameState gameState;

    private StructureId id;

    private StructureImpl fixture;

    @BeforeEach
    void setup(SeededRng random) {
        data = random.get();
        id = random.get();
        fixture = new StructureImpl(data, gameState, id);
        when(gameState.getRuleset()).thenReturn(ruleset);
        when(ruleset.getStructureType(data.getTypeId())).thenReturn(type);
    }

    @Test
    void getType() {
        assertThat(fixture.getType()).isEqualTo(type);
    }

    @Test
    void setType(SeededRng random) {
        var anotherTypeId = random.<StructureTypeId>get();
        when(anotherType.getId()).thenReturn(anotherTypeId);
        assertThat(fixture.setType(anotherType)).isSameAs(fixture);
        assertThat(data.getTypeId()).isEqualTo(anotherTypeId);
        verify(gameState).invalidateCache();
    }

    @Test
    void getTags(SeededRng random) {
        var value = Set.<StructureTag>of(random.get(), random.get());
        when(gameState.evaluate(new StructureTagQuery(fixture))).thenReturn(value);
        assertThat(fixture.getTags()).isEqualTo(value);
    }

    @Test
    void getVision(SeededRng random) {
        var value = random.nextInt();
        when(gameState.evaluate(new VisionRangeQuery(fixture))).thenReturn(value);
        assertThat(fixture.getVision()).isEqualTo(value);
    }

    @Test
    void getHp() {
        assertThat(fixture.getHp()).isEqualTo(data.getHp());
    }

    @Test
    void setHp(SeededRng random) {
        var hp = random.not(data.getHp(), Hp.ZERO);
        assertThat(fixture.setHp(hp)).isSameAs(fixture);
        assertThat(fixture.getHp()).isEqualTo(hp);
        assertThat(data.getHp()).isEqualTo(hp);
        verify(gameState).invalidateCache();
    }

    @Test
    void setHp_WhenTheValueIsZero_ThenDestroysTheStructure() {
        assertThat(fixture.setHp(Hp.ZERO)).isSameAs(fixture);
        assertThat(fixture.getHp()).isEqualTo(Hp.ZERO);
        assertThat(data.getHp()).isEqualTo(Hp.ZERO);
        verify(gameState).evaluate(new StructureDestructionEvent(fixture));
    }

    @Test
    void getTile() {
        when(gameState.getTiles()).thenReturn(new TreeMap<>(Map.of(id.tileId(), tile)));
        assertThat(fixture.getTile()).isEqualTo(tile);
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void isIncomplete(boolean incomplete) {
        data.setIncomplete(incomplete);
        assertThat(fixture.isIncomplete()).isEqualTo(incomplete);
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void setIncomplete(boolean incomplete) {
        assertThat(fixture.setIncomplete(incomplete)).isSameAs(fixture);
        assertThat(fixture.isIncomplete()).isEqualTo(incomplete);
        assertThat(data.isIncomplete()).isEqualTo(incomplete);
        verify(gameState).invalidateCache();
    }

    @Test
    void getOwner(SeededRng random) {
        var players = List.of(alice, bob, charlie, dave);
        when(gameState.getPlayers()).thenReturn(players);
        var index = random.nextInt(4);
        data.setPlayerId(new PlayerId(index));
        assertThat(fixture.getOwner()).isEqualTo(players.get(index));
    }

    @Test
    void getOwner_WhenThePlayerIdIsNull_ThenNull() {
        data.setPlayerId(null);
        assertThat(fixture.getOwner()).isNull();
    }

    @Test
    void setOwner(SeededRng random) {
        var playerId = random.<PlayerId>get();
        when(alice.getId()).thenReturn(playerId);
        assertThat(fixture.setOwner(alice)).isSameAs(fixture);
        assertThat(data.getPlayerId()).isEqualTo(playerId);
        verify(gameState).invalidateCache();
    }

    @Test
    void setOwner_Null(SeededRng random) {
        data.setPlayerId(random.get());
        fixture.setOwner(null);
        assertThat(data.getPlayerId()).isNull();
        verify(gameState).invalidateCache();
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void getActions(boolean incomplete) {
        data.setIncomplete(incomplete);
        when(type.getActions()).thenReturn(List.of(action0, action1));
        if (incomplete) {
            assertThat(fixture.getActions()).isEmpty();
        } else {
            assertThat(fixture.getActions()).containsExactly(action0, action1);
        }
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void getRules(boolean hasOwner, SeededRng random) {
        var players = List.of(alice, bob, charlie, dave);
        when(gameState.getPlayers()).thenReturn(players);
        var index = random.nextInt(4);
        data.setPlayerId(hasOwner ? new PlayerId(index) : null);
        when(players.get(index).getRules()).thenReturn(List.of(rule));
        if (hasOwner) {
            assertThat(fixture.getRules()).containsExactly(type, rule);
        } else {
            assertThat(fixture.getRules()).containsExactly(type);
        }
    }

}
