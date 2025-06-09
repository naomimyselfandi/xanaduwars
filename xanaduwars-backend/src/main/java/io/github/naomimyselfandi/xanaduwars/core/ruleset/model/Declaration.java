package io.github.naomimyselfandi.xanaduwars.core.ruleset.model;

import io.github.naomimyselfandi.xanaduwars.core.scripting.ScriptConstant;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Set;

/// An independent value declared by a ruleset.
public sealed interface Declaration extends ScriptConstant permits ActorType, PhysicalType, Spell {

    /// This declaration's unique name.
    Name name();

    /// Any tags that apply to this declaration.
    @Unmodifiable Set<? extends Tag> tags();

}
