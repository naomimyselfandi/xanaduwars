package io.github.naomimyselfandi.xanaduwars.map.entity;

import io.github.naomimyselfandi.xanaduwars.account.entity.Account;
import io.github.naomimyselfandi.xanaduwars.util.Id;
import io.github.naomimyselfandi.xanaduwars.util.NotCovered;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/// A repository that stores game maps.
public interface GameMapRepository extends JpaRepository<GameMap, Id<GameMap>>, JpaSpecificationExecutor<GameMap> {

    /// Check if a map name is in use.
    boolean existsByName(String name);

    /// The information about a map needed for access checks.
    @NotCovered // Trivial
    record PermissionDto(Id<Account> authorId, GameMap.Status status) {}

    /// Find the permission information for a map.
    @Query("SELECT g.author.id, g.status FROM GameMap g WHERE g.id = ?1")
    Optional<PermissionDto> findPermissionInfo(Id<GameMap> id);

    /// Find an account by ID and project it into a DTO.
    @Transactional(readOnly = true)
    <T> Optional<T> findById(Id<GameMap> id, Class<T> representation);

}
