package io.github.naomimyselfandi.xanaduwars.map.controller;

import io.github.naomimyselfandi.xanaduwars.account.dto.UserDetailsDto;
import io.github.naomimyselfandi.xanaduwars.account.entity.Account;
import io.github.naomimyselfandi.xanaduwars.account.service.RoleService;
import io.github.naomimyselfandi.xanaduwars.account.value.Role;
import io.github.naomimyselfandi.xanaduwars.auth.service.AuthService;
import io.github.naomimyselfandi.xanaduwars.map.entity.GameMap;
import io.github.naomimyselfandi.xanaduwars.map.entity.GameMapRepository;
import io.github.naomimyselfandi.xanaduwars.util.Id;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import static io.github.naomimyselfandi.xanaduwars.map.entity.GameMap.Status.*;

@Component
@RequiredArgsConstructor
class GameMapAccessPolicy {

    private final AuthService authService;
    private final RoleService roleService;
    private final GameMapRepository gameMapRepository;

    public boolean canAccess(Id<GameMap> id) {
        return gameMapRepository
                .findPermissionInfo(id)
                .map(it -> canAccess(authService.get(), it.authorId(), it.status()))
                .orElse(true);
    }

    public boolean canUpdate(Id<GameMap> id) {
        return gameMapRepository
                .findPermissionInfo(id)
                .map(it -> canUpdate(authService.get(), it.authorId(), it.status()))
                .orElse(true);
    }

    public boolean canUpdateStatus(Id<GameMap> id, GameMap.Status newStatus) {
        return gameMapRepository
                .findPermissionInfo(id)
                .map(it -> canUpdateStatus(authService.get(), it.authorId(), it.status(), newStatus))
                .orElse(true);
    }

    private static boolean canAccess(UserDetailsDto user, Id<Account> authorId, GameMap.Status status) {
        return user.id().equals(authorId) || status != UNPUBLISHED;
    }

    private static boolean canUpdate(UserDetailsDto user, Id<Account> authorId, GameMap.Status status) {
        return user.id().equals(authorId) && status != OFFICIAL;
    }

    private boolean canUpdateStatus(
            UserDetailsDto user,
            Id<Account> authorId,
            GameMap.Status oldStatus,
            GameMap.Status newStatus
    ) {
        if (newStatus == oldStatus) {
            return false; // for consistency
        } else if (newStatus == OFFICIAL) {
            return roleService.hasRole(user, Role.JUDGE) && oldStatus == SUBMITTED;
        } else if (oldStatus == OFFICIAL) {
            return roleService.hasRole(user, Role.JUDGE) && newStatus == PUBLISHED;
        } else if (oldStatus == SUBMITTED && newStatus == PUBLISHED && roleService.hasRole(user, Role.JUDGE)) {
            return true;
        } else {
            return user.id().equals(authorId);
        }
    }

}
