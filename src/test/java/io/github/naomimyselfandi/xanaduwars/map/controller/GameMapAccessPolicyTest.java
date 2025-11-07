package io.github.naomimyselfandi.xanaduwars.map.controller;

import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.account.dto.UserDetailsDto;
import io.github.naomimyselfandi.xanaduwars.account.service.RoleService;
import io.github.naomimyselfandi.xanaduwars.account.value.Role;
import io.github.naomimyselfandi.xanaduwars.auth.service.AuthService;
import io.github.naomimyselfandi.xanaduwars.map.entity.GameMap;
import io.github.naomimyselfandi.xanaduwars.map.entity.GameMapRepository;
import io.github.naomimyselfandi.xanaduwars.testing.SeededRng;
import io.github.naomimyselfandi.xanaduwars.util.Id;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith({MockitoExtension.class, SeededRandomExtension.class})
class GameMapAccessPolicyTest {

    @Mock
    private AuthService authService;

    @Mock
    private RoleService roleService;

    @Mock
    private GameMapRepository gameMapRepository;

    @InjectMocks
    private GameMapAccessPolicy fixture;

    private UserDetailsDto user;

    private Id<GameMap> id;

    private SeededRng random;

    @BeforeEach
    void setup(SeededRng random) {
        this.random = random;
        user = random.get();
        id = random.get();
        lenient().when(authService.get()).thenReturn(user);
    }

    @ParameterizedTest
    @CsvSource(textBlock = """
            true,UNPUBLISHED,true
            true,PUBLISHED,true
            true,SUBMITTED,true
            true,OFFICIAL,true
            false,UNPUBLISHED,false
            false,PUBLISHED,true
            false,SUBMITTED,true
            false,OFFICIAL,true
            """)
    void canAccess(boolean isAuthor, GameMap.Status status, boolean expected) {
        setPermissionInfo(isAuthor, status);
        assertThat(fixture.canAccess(id)).isEqualTo(expected);
    }

    @Test
    void canAccess_WhenTheMapDoesNotExist_ThenTrue() {
        assertThat(fixture.canAccess(id)).isTrue();
    }

    @ParameterizedTest
    @CsvSource(textBlock = """
            true,UNPUBLISHED,true
            true,PUBLISHED,true
            true,SUBMITTED,true
            true,OFFICIAL,false
            false,UNPUBLISHED,false
            false,PUBLISHED,false
            false,SUBMITTED,false
            false,OFFICIAL,false
            """)
    void canUpdate(boolean isAuthor, GameMap.Status status, boolean expected) {
        setPermissionInfo(isAuthor, status);
        assertThat(fixture.canUpdate(id)).isEqualTo(expected);
    }

    @Test
    void canUpdate_WhenTheMapDoesNotExist_ThenTrue() {
        assertThat(fixture.canUpdate(id)).isTrue();
    }

    @ParameterizedTest
    @CsvSource(textBlock = """
            false,false,UNPUBLISHED,UNPUBLISHED,false
            false,false,UNPUBLISHED,PUBLISHED,false
            false,false,UNPUBLISHED,SUBMITTED,false
            false,false,UNPUBLISHED,OFFICIAL,false
            false,false,PUBLISHED,UNPUBLISHED,false
            false,false,PUBLISHED,PUBLISHED,false
            false,false,PUBLISHED,SUBMITTED,false
            false,false,PUBLISHED,OFFICIAL,false
            false,false,SUBMITTED,UNPUBLISHED,false
            false,false,SUBMITTED,PUBLISHED,false
            false,false,SUBMITTED,SUBMITTED,false
            false,false,SUBMITTED,OFFICIAL,false
            false,false,OFFICIAL,UNPUBLISHED,false
            false,false,OFFICIAL,PUBLISHED,false
            false,false,OFFICIAL,SUBMITTED,false
            false,false,OFFICIAL,OFFICIAL,false
            true,false,UNPUBLISHED,UNPUBLISHED,false
            true,false,UNPUBLISHED,PUBLISHED,true
            true,false,UNPUBLISHED,SUBMITTED,true
            true,false,UNPUBLISHED,OFFICIAL,false
            true,false,PUBLISHED,UNPUBLISHED,true
            true,false,PUBLISHED,PUBLISHED,false
            true,false,PUBLISHED,SUBMITTED,true
            true,false,PUBLISHED,OFFICIAL,false
            true,false,SUBMITTED,UNPUBLISHED,true
            true,false,SUBMITTED,PUBLISHED,true
            true,false,SUBMITTED,SUBMITTED,false
            true,false,SUBMITTED,OFFICIAL,false
            true,false,OFFICIAL,UNPUBLISHED,false
            true,false,OFFICIAL,PUBLISHED,false
            true,false,OFFICIAL,SUBMITTED,false
            true,false,OFFICIAL,OFFICIAL,false
            false,true,UNPUBLISHED,UNPUBLISHED,false
            false,true,UNPUBLISHED,PUBLISHED,false
            false,true,UNPUBLISHED,SUBMITTED,false
            false,true,UNPUBLISHED,OFFICIAL,false
            false,true,PUBLISHED,UNPUBLISHED,false
            false,true,PUBLISHED,PUBLISHED,false
            false,true,PUBLISHED,SUBMITTED,false
            false,true,PUBLISHED,OFFICIAL,false
            false,true,SUBMITTED,UNPUBLISHED,false
            false,true,SUBMITTED,PUBLISHED,true
            false,true,SUBMITTED,SUBMITTED,false
            false,true,SUBMITTED,OFFICIAL,true
            false,true,OFFICIAL,UNPUBLISHED,false
            false,true,OFFICIAL,PUBLISHED,true
            false,true,OFFICIAL,SUBMITTED,false
            false,true,OFFICIAL,OFFICIAL,false
            true,true,SUBMITTED,UNPUBLISHED,true
            """)
    void canUpdateStatus(
            boolean isAuthor,
            boolean isJudge,
            GameMap.Status oldStatus,
            GameMap.Status newStatus,
            boolean expected
    ) {
        setPermissionInfo(isAuthor, oldStatus);
        lenient().when(roleService.hasRole(user, Role.JUDGE)).thenReturn(isJudge);
        assertThat(fixture.canUpdateStatus(id, newStatus)).isEqualTo(expected);
    }

    @EnumSource
    @ParameterizedTest
    void canUpdateStatus_WhenTheMapDoesNotExist_ThenTrue(GameMap.Status status) {
        assertThat(fixture.canUpdateStatus(id, status)).isTrue();
    }

    private void setPermissionInfo(boolean isAuthor, GameMap.Status status) {
        var authorId = isAuthor ? user.id() : random.not(user.id());
        var permissionInfo = new GameMapRepository.PermissionDto(authorId, status);
        when(gameMapRepository.findPermissionInfo(id)).thenReturn(Optional.of(permissionInfo));
    }

}
