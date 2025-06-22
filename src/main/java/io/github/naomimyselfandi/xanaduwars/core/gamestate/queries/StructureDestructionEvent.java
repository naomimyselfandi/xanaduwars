package io.github.naomimyselfandi.xanaduwars.core.gamestate.queries;

import io.github.naomimyselfandi.xanaduwars.core.gamestate.Structure;
import io.github.naomimyselfandi.xanaduwars.core.scripting.SimpleEvent;

/// An event that indicates a structure was destroyed.
public record StructureDestructionEvent(Structure subject) implements SimpleEvent {}
