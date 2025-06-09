package io.github.naomimyselfandi.xanaduwars.core.scripting;

import java.util.stream.Stream;

/// The source of the global game rules and scripting constants.
public interface GlobalRuleSource extends RuleSource {

    /// Provide scripting constants.
    Stream<ScriptConstant> constants();

}
