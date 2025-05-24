package io.github.naomimyselfandi.xanaduwars.core;

import io.github.naomimyselfandi.xanaduwars.core.wrapper.Name;
import io.github.naomimyselfandi.xanaduwars.core.wrapper.TagSet;
import io.github.naomimyselfandi.xanaduwars.core.wrapper.Tagged;
import jakarta.validation.constraints.PositiveOrZero;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;

/// A type of object defined by some version of the game rules.
///
/// This interface represents a category of game-defined entities such as units,
/// spells, tiles, or commanders. Each type may have its own behavior, tagging,
/// and rule definitions that affect how it interacts with the rest of the game
/// system.
public sealed interface Type extends Tagged permits NodeType, Commander, SpellType {

    /// Any rules that apply to queries *initiated by* instances of this type.
    /// These rules typically modify behavior that instances of this type
    /// actively perform. For example, a unit type might define a rule that
    /// modifies the damage a unit of that type inflicts when it attacks. These
    /// rules can also apply to associated objects; for example, a tile's rules
    /// apply to units on that tile.
    @Unmodifiable List<Rule<?, ?>> rules();

    /// Any rules that apply to queries *targeting* instances of this type.
    /// These rules typically modify behavior other objects perform on instances
    /// of this type. For example, a unit type might define a rule that modifies
    /// the damage a unit of that type receives when attacked. These rules can
    /// also apply to associated objects; for example, a tile's rules apply to
    /// units on that tile.
    @Unmodifiable List<Rule<?, ?>> targetRules();

    /// The unique name of this type, used as an identifier. Valid names begin
    /// with an uppercase letter and contain only alphanumeric characters.
    @Override
    Name name();

    /// This type's index. This is separate for each kind of type. For example,
    /// the first unit type and tile type both have an index of 0, the second
    /// unit type and tile type both have an index of 1, and so on.
    @PositiveOrZero int index();

    /// Any tags associated with this type. By default, these tags apply to all
    /// instances of the type, though specific rules may add or remove tags at
    /// runtime.
    @Override
    TagSet tags();

}
