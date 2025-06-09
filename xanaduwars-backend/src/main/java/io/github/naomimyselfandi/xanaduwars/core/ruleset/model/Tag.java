package io.github.naomimyselfandi.xanaduwars.core.ruleset.model;

import io.github.naomimyselfandi.xanaduwars.core.scripting.ScriptConstant;

/// A tag used to categorize related objects.
public sealed interface Tag extends ScriptConstant permits SpellTag, StructureTag, TileTag, UnitTag {}
