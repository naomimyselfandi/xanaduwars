package io.github.naomimyselfandi.xanaduwars.core.scripting.evaluator;

import io.github.naomimyselfandi.xanaduwars.core.scripting.*;
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
                query.prologue().stream(),
                globals.getRules().stream().flatMap(queryName),
                extraRules(query).stream().flatMap(queryName),
                query.epilogue().stream()
        ).flatMap(Function.identity()).toList();
    }

    private List<Rule> extraRules(Query<?> query) {
        return query.subject() instanceof RuleSource subject ? subject.getRules() : List.of();
    }

}
