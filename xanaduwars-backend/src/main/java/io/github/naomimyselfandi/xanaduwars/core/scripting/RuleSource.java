package io.github.naomimyselfandi.xanaduwars.core.scripting;

import java.util.stream.Stream;

/// An object that can provide game rules. A rule source can provide the global
/// rules, but if the [subject][Query#subject()] of a query is a rule source,
/// its rules are also considered.
public interface RuleSource {

    /// Provide game rules.
    Stream<Rule> rules();

}
