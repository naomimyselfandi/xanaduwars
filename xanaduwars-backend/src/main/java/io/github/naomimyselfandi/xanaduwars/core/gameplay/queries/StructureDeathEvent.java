package io.github.naomimyselfandi.xanaduwars.core.gameplay.queries;

import io.github.naomimyselfandi.xanaduwars.core.gameplay.state.Structure;

/// An event indicating a structure was destroyed.
public record StructureDeathEvent(Structure subject) implements Event {}
