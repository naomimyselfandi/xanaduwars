package io.github.naomimyselfandi.xanaduwars.game.service;

import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.account.entity.Account;
import io.github.naomimyselfandi.xanaduwars.auth.service.AuthService;
import io.github.naomimyselfandi.xanaduwars.core.gamestate.PlayerId;
import io.github.naomimyselfandi.xanaduwars.core.gamestate.Turn;
import io.github.naomimyselfandi.xanaduwars.core.gamestate.entity.GameStateData;
import io.github.naomimyselfandi.xanaduwars.game.entity.Game;
import io.github.naomimyselfandi.xanaduwars.game.entity.PlayerSlot;
import io.github.naomimyselfandi.xanaduwars.testing.SeededRng;
import io.github.naomimyselfandi.xanaduwars.util.Id;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@MockitoSettings(strictness = Strictness.LENIENT)
@ExtendWith({MockitoExtension.class, SeededRandomExtension.class})
class GameGuardTest {

    private Id<Account> accountId;

    private Game game;

    private PlayerId playerId;

    @Mock
    private AuthService authService;

    @InjectMocks
    private GameGuard fixture;

    private SeededRng random;

    @BeforeEach
    void setup(SeededRng random) {
        accountId = random.get();
        game = new Game();
        playerId = random.get();
        when(authService.getId()).thenReturn(accountId);
        this.random = random;
    }

    @ParameterizedTest
    @CsvSource(textBlock = """
            AUTHENTICATED_USER,ANOTHER_USER,true
            ANOTHER_USER,AUTHENTICATED_USER,true
            ANOTHER_USER,ANOTHER_USER,false
            """)
    void isPlayer(TestUser testUser0, TestUser testUser1, boolean expected) {
        game.setHost(new Account().setId(random.get()));
        setAccountId(testUser0, new PlayerId(0));
        setAccountId(testUser1, new PlayerId(1));
        assertThat(fixture.isPlayer(game)).isEqualTo(expected);
    }

    @Test
    void isPlayer_WhenTheAccountIsTheHost_ThenTrue() {
        game.setHost(new Account().setId(accountId));
        assertThat(fixture.isPlayer(game)).isTrue();
    }

    @ParameterizedTest
    @CsvSource(textBlock = """
            AUTHENTICATED_USER,ANOTHER_USER,0,true
            AUTHENTICATED_USER,ANOTHER_USER,1,false
            ANOTHER_USER,AUTHENTICATED_USER,0,false
            ANOTHER_USER,AUTHENTICATED_USER,1,true
            ANOTHER_USER,ANOTHER_USER,0,false
            """)
    void isPlayer(TestUser testUser0, TestUser testUser1, int playerId, boolean expected) {
        setAccountId(testUser0, new PlayerId(0));
        setAccountId(testUser1, new PlayerId(1));
        assertThat(fixture.isPlayer(game, new PlayerId(playerId))).isEqualTo(expected);
    }

    @ParameterizedTest
    @CsvSource(textBlock = """
            AUTHENTICATED_USER,ANOTHER_USER,true
            ANOTHER_USER,AUTHENTICATED_USER,true
            ANOTHER_USER,ANOTHER_USER,false
            """)
    void canViewMetadata(TestUser testUser0, TestUser testUser1, boolean expected) {
        game.setHost(new Account().setId(random.get()));
        setAccountId(testUser0, new PlayerId(0));
        setAccountId(testUser1, new PlayerId(1));
        assertThat(fixture.canViewMetadata(game)).isEqualTo(expected);
    }

    @ParameterizedTest
    @CsvSource(textBlock = """
            ONGOING,AUTHENTICATED_USER,true
            ONGOING,ANOTHER_USER,false
            ONGOING,NO_USER,false
            FINISHED,AUTHENTICATED_USER,true
            FINISHED,ANOTHER_USER,false
            FINISHED,NO_USER,false
            PENDING,AUTHENTICATED_USER,true
            CANCELED,AUTHENTICATED_USER,true
            """)
    void canView(Game.Status status, TestUser testUser, boolean expected) {
        game.setStatus(status);
        setAccountId(testUser, playerId);
        assertThat(fixture.canView(game, playerId)).isEqualTo(expected);
    }

    @ParameterizedTest
    @CsvSource(textBlock = """
            PENDING,false
            ONGOING,false
            FINISHED,true
            CANCELED,false
            """)
    void canView_Null(Game.Status status, boolean expected) {
        game.setStatus(status);
        assertThat(fixture.canView(game, null)).isEqualTo(expected);
    }

    @ParameterizedTest
    @CsvSource(textBlock = """
            PENDING,AUTHENTICATED_USER,false
            ONGOING,AUTHENTICATED_USER,true
            ONGOING,ANOTHER_USER,false
            ONGOING,NO_USER,false
            FINISHED,AUTHENTICATED_USER,false
            CANCELED,AUTHENTICATED_USER,false
            """)
    void isActivePlayer(Game.Status status, TestUser testUser, boolean expected) {
        game.setStatus(status);
        setActivePlayerAccountId(testUser);
        assertThat(fixture.isActivePlayer(game)).isEqualTo(expected);
    }

    private enum TestUser {AUTHENTICATED_USER, ANOTHER_USER, NO_USER}

    private void setAccountId(TestUser testUser, PlayerId playerId) {
        var slot = switch (testUser) {
            case AUTHENTICATED_USER -> new PlayerSlot().setAccount(new Account().setId(accountId));
            case ANOTHER_USER -> new PlayerSlot().setAccount(new Account().setId(random.get()));
            case NO_USER -> null;
        };
        game.getPlayerSlots().put(playerId, slot);
    }

    private void setActivePlayerAccountId(TestUser testUser) {
        for (var i = 0; i < 4; i++) {
            game.getPlayerSlots().put(new PlayerId(i), random.get());
        }
        var playerId = random.pick(game.getPlayerSlots().keySet());
        var turn = new Turn(random.nextInt(100) * 4 + playerId.playerId());
        game.setGameStateData(new GameStateData().setTurn(turn));
        setAccountId(testUser, playerId);
    }

}
