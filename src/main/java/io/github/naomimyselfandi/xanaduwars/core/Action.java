package io.github.naomimyselfandi.xanaduwars.core;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import io.github.naomimyselfandi.xanaduwars.core.wrapper.Name;
import io.github.naomimyselfandi.xanaduwars.core.wrapper.Tagged;
import io.github.naomimyselfandi.xanaduwars.ext.ExcludeFromCoverageReport;

import java.util.stream.Stream;

/// An action that an element can execute.
@ExcludeFromCoverageReport
public interface Action<S extends Element, T> extends Tagged {

    /// This action's name. Action names are not necessarily unique.
    @Override
    Name name();

    /// Describe this action's target type.
    JavaType targetType(TypeFactory typeFactory);

    /// Find all possible targets for this action in some game state. This only
    /// considers the type, not any details of the game state. As a special
    /// case, the [action representing unit movement][Ruleset#moveAction()] may
    /// return an empty stream, since the result would be very large otherwise.
    Stream<T> enumerateTargets(GameState gameState);

    /// Perform any *structural validation* associated with this action. If this
    /// fails, it indicates that the given target and user are nonsensical for
    /// this action. This does not consider game rules; a `true` result
    /// indicates that executing this action with the same inputs is
    /// well-defined, not that it is legal in game terms.
    default boolean test(S user, T target) {
        return true;
    }

    /// Instruct an element to execute this action on a target.
    ///
    /// If [#test(Object, Element)] fails for some input, calling this method
    /// with the same input is not well-defined.
    Execution execute(S user, T target);

    /// Calculate a cost for an element to use this action on a target.
    default int cost(Resource resource, S user, T target) {
        return 0;
    }

}
