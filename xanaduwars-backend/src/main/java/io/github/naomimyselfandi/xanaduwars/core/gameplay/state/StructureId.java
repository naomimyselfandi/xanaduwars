package io.github.naomimyselfandi.xanaduwars.core.gameplay.state;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Embeddable;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

/// The ID of a structure. Since each structure belongs to a unique tile
/// which never changes, a structure's ID is given by its tile's ID.
@Embeddable
@SuppressWarnings("com.intellij.jpb.NoArgsConstructorInspection")
public record StructureId(@JsonProperty("structureId") @NotNull @Valid TileId tileId) implements AssetId {}
