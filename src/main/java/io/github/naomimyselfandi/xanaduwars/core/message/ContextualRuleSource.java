package io.github.naomimyselfandi.xanaduwars.core.message;

import java.util.stream.Stream;

public interface ContextualRuleSource {

    Stream<Rule> getContextualRules();

}
