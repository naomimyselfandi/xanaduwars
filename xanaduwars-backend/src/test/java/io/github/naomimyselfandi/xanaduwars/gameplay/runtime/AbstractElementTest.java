package io.github.naomimyselfandi.xanaduwars.gameplay.runtime;

import io.github.naomimyselfandi.xanaduwars.testing.SeededRng;
import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.gameplay.Player;
import io.github.naomimyselfandi.xanaduwars.gameplay.Tile;
import io.github.naomimyselfandi.xanaduwars.gameplay.UnitType;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith({MockitoExtension.class, SeededRandomExtension.class})
class AbstractElementTest {

    private interface Id {}

    @Mock
    private Player player, anotherPlayer;

    @Mock
    private Tile anotherElement;

    @Mock
    private AugmentedGameState gameState;

    @Mock
    private Id id;

    @Mock
    private UnitType type;

    private final class Fixture extends AbstractElement<Object, UnitType> {

        private final @Nullable Player owner;

        Fixture(AugmentedGameState gameState, Id id, @Nullable Player owner) {
            super(gameState, id);
            this.owner = owner;
        }

        @Override
        public UnitType type() {
            return type;
        }

        @Override
        public Optional<Player> owner() {
            return Optional.ofNullable(owner);
        }

    }

    private AbstractElement<?, ?> fixture;

    @BeforeEach
    void setup() {
        fixture = new Fixture(gameState, id, player);
    }

    @RepeatedTest(3)
    void name(SeededRng random) {
        var name = random.nextName();
        when(type.name()).thenReturn(name);
        assertThat(fixture.name()).isEqualTo(name);
    }

    @RepeatedTest(3)
    void isAlly_WhenBothElementsAreOnTheSameTeam_ThenTrue(SeededRng random) {
        var team = random.nextInt();
        when(player.team()).thenReturn(team);
        when(anotherPlayer.team()).thenReturn(team);
        when(anotherElement.owner()).thenReturn(Optional.of(anotherPlayer));
        assertThat(fixture.isAlly(anotherElement)).isTrue();
    }

    @RepeatedTest(3)
    void isAlly_WhenBothElementsAreOnDifferentTeams_ThenFalse(SeededRng random) {
        int team, anotherTeam;
        do {
            team = random.nextInt();
            anotherTeam = random.nextInt();
        } while(team == anotherTeam);
        when(player.team()).thenReturn(team);
        when(anotherPlayer.team()).thenReturn(anotherTeam);
        when(anotherElement.owner()).thenReturn(Optional.of(anotherPlayer));
        assertThat(fixture.isAlly(anotherElement)).isFalse();
    }

    @Test
    void isAlly_WhenEitherElementIsUnowned_ThenChecksIfBothAreUnowned() {
        assertThat(fixture.isAlly(anotherElement)).isFalse();
        fixture = new Fixture(gameState, id, null);
        assertThat(fixture.isAlly(anotherElement)).isTrue();
        when(anotherElement.owner()).thenReturn(Optional.of(anotherPlayer));
        assertThat(fixture.isAlly(anotherElement)).isFalse();
    }

    @RepeatedTest(3)
    void testToString(SeededRng random) {
        var name = random.nextName();
        when(type.name()).thenReturn(name);
        when(id.toString()).thenReturn(random.nextUUID().toString());
        assertThat(fixture).hasToString("%s[%s]", id, name);
    }

}
