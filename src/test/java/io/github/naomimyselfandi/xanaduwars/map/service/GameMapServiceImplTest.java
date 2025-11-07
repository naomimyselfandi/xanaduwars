package io.github.naomimyselfandi.xanaduwars.map.service;

import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.account.dto.UserDetailsDto;
import io.github.naomimyselfandi.xanaduwars.auth.service.AuthService;
import io.github.naomimyselfandi.xanaduwars.auth.value.UnauthorizedException;
import io.github.naomimyselfandi.xanaduwars.map.dto.GameMapDto;
import io.github.naomimyselfandi.xanaduwars.map.dto.GameMapUpdateDto;
import io.github.naomimyselfandi.xanaduwars.map.entity.GameMap;
import io.github.naomimyselfandi.xanaduwars.map.entity.GameMapRepository;
import io.github.naomimyselfandi.xanaduwars.testing.SeededRng;
import io.github.naomimyselfandi.xanaduwars.util.ConflictException;
import io.github.naomimyselfandi.xanaduwars.util.Id;
import io.github.naomimyselfandi.xanaduwars.util.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith({MockitoExtension.class, SeededRandomExtension.class})
class GameMapServiceImplTest {

    @Mock
    private AuthService authService;

    @Mock
    private GameMapServiceHelper helper;

    @Mock
    private GameMapRepository gameMapRepository;

    @InjectMocks
    private GameMapServiceImpl fixture;

    private Id<GameMap> id;

    private GameMap gameMap;

    private GameMapDto dto;

    private GameMapUpdateDto request;

    @BeforeEach
    void setup(SeededRng random) {
        id = random.get();
        gameMap = random.get();
        dto = random.get();
        request = random.get();
    }

    @Test
    void get() {
        when(gameMapRepository.findById(id)).thenReturn(Optional.of(gameMap));
        when(helper.convert(gameMap)).thenReturn(dto);
        assertThat(fixture.get(id)).isEqualTo(dto);
    }

    @Test
    void get_WhenTheMapDoesNotExist_ThenThrows() {
        when(gameMapRepository.findById(id)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> fixture.get(id))
                .isInstanceOf(NotFoundException.class)
                .hasMessage(new NotFoundException(id).getMessage());
    }

    @Test
    void create(SeededRng random) {
        var user = random.<UserDetailsDto>get();
        when(authService.loadForAuthenticatedUser()).thenReturn(Optional.of(user));
        doAnswer(invocation -> {
            var map = invocation.<GameMap>getArgument(0);
            assertThat(map.getAuthor().getId()).isEqualTo(user.id());
            assertThat(map.getStatus()).isEqualTo(GameMap.Status.UNPUBLISHED);
            return null;
        }).when(helper).update(any(), eq(request));
        when(gameMapRepository.save(any())).then(invocation -> {
            var map = invocation.<GameMap>getArgument(0);
            verify(helper).update(map, request);
            return gameMap;
        });
        when(helper.convert(gameMap)).thenReturn(dto);
        assertThat(fixture.create(request)).isEqualTo(dto);
    }

    @Test
    void create_WhenNotLoggedIn_ThenThrows() {
        when(authService.loadForAuthenticatedUser()).thenReturn(Optional.empty());
        assertThatThrownBy(() -> fixture.create(request))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessage(UnauthorizedException.INVALID_CREDENTIALS);
    }

    @Test
    void create_WhenTheNameIsInUse_ThenThrows(SeededRng random) {
        var user = random.<UserDetailsDto>get();
        when(authService.loadForAuthenticatedUser()).thenReturn(Optional.of(user));
        when(gameMapRepository.existsByName(request.name())).thenReturn(true);
        assertThatThrownBy(() -> fixture.create(request))
                .isInstanceOf(ConflictException.class)
                .hasMessage("Map name '%s' is unavailable.", request.name());
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void update(boolean changingName) {
        if (!changingName) {
            var name = request.name();
            gameMap.setName(name);
            lenient().when(gameMapRepository.existsByName(name)).thenReturn(true);
        }
        when(gameMapRepository.findById(id)).thenReturn(Optional.of(gameMap));
        when(helper.convert(gameMap)).then(_ -> {
            verify(helper).update(gameMap, request);
            return dto;
        });
        assertThat(fixture.update(id, request)).isEqualTo(dto);
    }

    @Test
    void update_WhenTheNameIsInUse_ThenThrows() {
        when(gameMapRepository.findById(id)).thenReturn(Optional.of(gameMap));
        when(gameMapRepository.existsByName(request.name())).thenReturn(true);
        assertThatThrownBy(() -> fixture.update(id, request))
                .isInstanceOf(ConflictException.class)
                .hasMessage("Map name '%s' is unavailable.", request.name());
    }

    @Test
    void update_WhenTheMapDoesNotExist_ThenThrows() {
        when(gameMapRepository.findById(id)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> fixture.update(id, request))
                .isInstanceOf(NotFoundException.class)
                .hasMessage(new NotFoundException(id).getMessage());
    }

    @EnumSource
    @ParameterizedTest
    void updateStatus(GameMap.Status status, SeededRng random) {
        gameMap.setStatus(random.not(status));
        when(gameMapRepository.findById(id)).thenReturn(Optional.of(gameMap));
        when(helper.convert(gameMap)).then(_ -> {
            assertThat(gameMap.getStatus()).isEqualTo(status);
            return dto;
        });
        assertThat(fixture.updateStatus(id, status)).isEqualTo(dto);
    }

    @EnumSource
    @ParameterizedTest
    void updateStatus_WhenTheMapDoesNotExist_ThenThrows(GameMap.Status status) {
        when(gameMapRepository.findById(id)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> fixture.updateStatus(id, status))
                .isInstanceOf(NotFoundException.class)
                .hasMessage(new NotFoundException(id).getMessage());
    }

}
