package io.github.naomimyselfandi.xanaduwars.core.scripting;

import java.util.Map;

/// The source of the global game rules and scripting constants.
public interface GlobalRuleSource extends RuleSource {

    /// Get the map of scripting constants.
    Map<String, Object> getScriptConstants();

}
