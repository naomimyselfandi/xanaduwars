package io.github.naomimyselfandi.xanaduwars.core.gamestate;

import java.io.Serializable;

/// The ID of a game state element.
public sealed interface ElementId extends Serializable permits NodeId, PlayerId, StructureId {}
