package io.github.naomimyselfandi.xanaduwars.gameplay;

import io.github.naomimyselfandi.xanaduwars.gameplay.value.Name;
import io.github.naomimyselfandi.xanaduwars.gameplay.value.TagSet;
import io.github.naomimyselfandi.xanaduwars.gameplay.value.Tagged;
import io.github.naomimyselfandi.xanaduwars.gameplay.value.TypeId;
import io.github.naomimyselfandi.xanaduwars.gameplay.query.Rule;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;

/// A type of object defined by some version of the game rules.
///
/// This interface represents a category of game-defined entities such as units,
/// spells, tiles, or commanders. Each type may have its own behavior, tagging,
/// and rule definitions that affect how it interacts with the rest of the game
/// system.
public sealed interface Type extends Tagged permits NodeType, Commander, SpellType {

    /// This type's unique ID.
    TypeId id();

    /// The unique name of this type, used as an identifier. Valid names begin
    /// with an uppercase letter and contain only alphanumeric characters.
    @Override
    Name name();

    /// Any tags associated with this type. By default, these tags apply to all
    /// instances of the type, though specific rules may add or remove tags at
    /// runtime.
    @Override
    TagSet tags();

    /// Any rules that apply to queries *initiated by* instances of this type.
    /// These rules typically modify behavior that instances of this type
    /// actively perform. For example, a unit type might define a rule that
    /// modifies the damage a unit of that type inflicts when it attacks. These
    /// rules can also apply to associated objects; for example, a tile's rules
    /// apply to units on that tile.
    @Unmodifiable List<Rule<?, ?>> subjectRules();

    /// Any rules that apply to queries *targeting* instances of this type.
    /// These rules typically modify behavior other objects perform on instances
    /// of this type. For example, a unit type might define a rule that modifies
    /// the damage a unit of that type receives when attacked. These rules can
    /// also apply to associated objects; for example, a tile's rules apply to
    /// units on that tile.
    @Unmodifiable List<Rule<?, ?>> targetRules();

}
