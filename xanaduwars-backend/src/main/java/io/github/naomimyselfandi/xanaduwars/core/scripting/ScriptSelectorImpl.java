package io.github.naomimyselfandi.xanaduwars.core.scripting;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

@Service
class ScriptSelectorImpl implements ScriptSelector {

    @Override
    public List<Script> select(GlobalRuleSource globals, Query<?> query) {
        var queryName = QueryName.of(query);
        return Stream.of(
                query.prologue(),
                globals.rules().flatMap(queryName),
                extraRules(query).flatMap(queryName),
                query.epilogue()
        ).flatMap(Function.identity()).toList();
    }

    private Stream<Rule> extraRules(Query<?> query) {
        return query.subject() instanceof RuleSource subject ? subject.rules() : Stream.empty();
    }

}
