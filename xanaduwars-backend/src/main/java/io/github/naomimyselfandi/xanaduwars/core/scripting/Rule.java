package io.github.naomimyselfandi.xanaduwars.core.scripting;

import org.jetbrains.annotations.Unmodifiable;

import java.util.Map;

/// A game rule. Game rules can be global, in which case their query handlers
/// apply to all queries. Commanders and spells also act as rules, and their
/// handlers are scoped based on a' query's subject. Specifically, a commander's
/// handlers apply while the subject is a player who is playing as the commander
/// or an asset owned by such a player, and a spell's handlers apply while the
/// subject is a player who has that spell active or an asset owned by such a
/// player.
public interface Rule {

    /// The query handlers associated with this rule.
    @Unmodifiable Map<QueryName, Script> getHandlers();

}
