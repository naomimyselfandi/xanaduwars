package io.github.naomimyselfandi.xanaduwars.game.service;

import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.account.entity.Account;
import io.github.naomimyselfandi.xanaduwars.core.gamestate.PlayerId;
import io.github.naomimyselfandi.xanaduwars.game.entity.Game;
import io.github.naomimyselfandi.xanaduwars.game.entity.PlayerSlot;
import io.github.naomimyselfandi.xanaduwars.testing.SeededRng;
import io.github.naomimyselfandi.xanaduwars.util.Id;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith({MockitoExtension.class, SeededRandomExtension.class})
class PlayerIdServiceImplTest {

    private Id<Account> accountId;

    private PlayerIdServiceImpl fixture;

    private SeededRng random;

    @BeforeEach
    void setup(SeededRng random) {
        accountId = random.get();
        fixture = new PlayerIdServiceImpl();
        this.random = random;
    }

    @ParameterizedTest
    @CsvSource(textBlock = """
            OTHER_ACCOUNT,OTHER_ACCOUNT,OTHER_ACCOUNT,
            OTHER_ACCOUNT,NO_ACCOUNT,OTHER_ACCOUNT,
            SAME_ACCOUNT,OTHER_ACCOUNT,OTHER_ACCOUNT,0
            SAME_ACCOUNT,NO_ACCOUNT,OTHER_ACCOUNT,0
            OTHER_ACCOUNT,OTHER_ACCOUNT,SAME_ACCOUNT,2
            OTHER_ACCOUNT,NO_ACCOUNT,SAME_ACCOUNT,2
            """)
    void findPlayerId(TestAccount account0, TestAccount account1, TestAccount account2, @Nullable Integer expected) {
        var game = new Game();
        initPlayerSlot(game, 0, account0);
        initPlayerSlot(game, 1, account1);
        initPlayerSlot(game, 2, account2);
        var playerId = Optional.ofNullable(expected).map(PlayerId::new).orElse(null);
        assertThat(fixture.findPlayerId(game, accountId)).isEqualTo(playerId);
    }

    private enum TestAccount {SAME_ACCOUNT, OTHER_ACCOUNT, NO_ACCOUNT}

    private void initPlayerSlot(Game game, int index, TestAccount testAccount) {
        var slots = game.getPlayerSlots();
        var _ = switch (testAccount) {
            case SAME_ACCOUNT -> {
                var slot = new PlayerSlot().setAccount(new Account().setId(accountId));
                yield slots.put(new PlayerId(index), slot);
            }
            case OTHER_ACCOUNT -> {
                var slot = new PlayerSlot().setAccount(new Account().setId(random.not(accountId)));
                yield slots.put(new PlayerId(index), slot);
            }
            case NO_ACCOUNT -> null;
        };
    }

}
