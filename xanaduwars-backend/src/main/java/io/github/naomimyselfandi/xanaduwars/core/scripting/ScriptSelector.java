package io.github.naomimyselfandi.xanaduwars.core.scripting;

import java.util.List;

interface ScriptSelector {

    List<Script> select(GlobalRuleSource globals, Query<?> query);

}
