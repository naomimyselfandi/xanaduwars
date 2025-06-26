package io.github.naomimyselfandi.xanaduwars.game.service;

import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.game.entity.Game;
import io.github.naomimyselfandi.xanaduwars.game.entity.GameRepository;
import io.github.naomimyselfandi.xanaduwars.testing.SeededRng;
import io.github.naomimyselfandi.xanaduwars.util.ConflictException;
import io.github.naomimyselfandi.xanaduwars.util.Id;
import io.github.naomimyselfandi.xanaduwars.util.NoSuchEntityException;
import lombok.SneakyThrows;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.concurrent.Callable;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith({MockitoExtension.class, SeededRandomExtension.class})
class GameFetcherImplTest {

    private Id<Game> id;

    private Game game;

    @Mock
    private GameRepository gameRepository;

    @InjectMocks
    private GameFetcherImpl fixture;

    @BeforeEach
    void setup(SeededRng random) {
        id = random.get();
    }

    @ParameterizedTest
    @CsvSource(textBlock = """
            ,
            PENDING,
            ONGOING,
            FINISHED,
            CANCELED,
            """)
    void fetch(@Nullable Game.Status status, @Nullable String conflictExceptionMessage) {
        createGame(status);
        test(() -> fixture.fetch(id), conflictExceptionMessage);
    }

    @ParameterizedTest
    @CsvSource(textBlock = """
            ,
            PENDING,
            ONGOING,The game has already started.
            FINISHED,The game has already ended.
            CANCELED,The game has already ended.
            """)
    void fetchPending(@Nullable Game.Status status, @Nullable String conflictExceptionMessage) {
        createGame(status);
        test(() -> fixture.fetchPending(id), conflictExceptionMessage);
    }

    @ParameterizedTest
    @CsvSource(textBlock = """
            ,
            PENDING,The game has not started yet.
            ONGOING,
            FINISHED,The game has already ended.
            CANCELED,The game has already ended.
            """)
    void fetchOngoing(@Nullable Game.Status status, @Nullable String conflictExceptionMessage) {
        createGame(status);
        test(() -> fixture.fetchOngoing(id), conflictExceptionMessage);
    }

    private void createGame(@Nullable Game.Status status) {
        if (status != null) {
            game = new Game().setStatus(status);
            when(gameRepository.findById(id)).thenReturn(Optional.of(game));
        }
    }

    @SneakyThrows
    private void test(Callable<Game> callback, @Nullable String conflictExceptionMessage) {
        if (conflictExceptionMessage != null) {
            assertThatThrownBy(callback::call).isInstanceOf(ConflictException.class).hasMessage(conflictExceptionMessage);
        } else if (game == null) {
            assertThatThrownBy(callback::call).isInstanceOf(NoSuchEntityException.class);
        } else {
            assertThat(callback.call()).isEqualTo(game);
        }
    }

}
