package io.github.naomimyselfandi.xanaduwars.gameplay.query;

import java.util.function.Consumer;
import java.util.stream.Stream;

/// A common query evaluator implementation. This implementation distinguishes
/// two kinds of rules: *global rules*, which can handle any query (of the
/// appropriate type), and *contextual rules*, which are only considered in
/// specific contexts. For example, contextual rules may be tied to a specific
/// unit, spell, or other game object. Both global and custom rules have their
/// own declaration order; the overall declaration order consists of the global
/// rules followed by any contextual rules, both in their own declaration order.
public abstract class AbstractQueryEvaluator implements QueryEvaluator {

    private static final Consumer<Rule<?, ?>> NOOP = _ -> {};

    /// Get the global rules, which are always applied before contextual rules.
    protected abstract Stream<Rule<?, ?>> globalRules();

    /// Returns any rules contextual rules that can to this query.
    ///
    /// Unlike global rules, which are always considered, contextual rules are
    /// only considered in specific contexts; examples include rules tied to a
    /// specific unit, spell, or other game object.
    ///
    /// Implementations may return an empty stream if no contextual rules apply.
    protected abstract Stream<Rule<?, ?>> contextualRules(Query<?> query);

    @Override
    public <Q extends Query<V>, V> V evaluate(Q query) {
        return evaluate(query, NOOP);
    }

    @Override
    public <Q extends Query<V>, V> V evaluate(Q query, Consumer<Rule<?, ?>> callback) {
        var value = query.defaultValue();
        // It's possible for the default value to trigger short-circuiting.
        if (query.shouldShortCircuit(value)) return value;
        // Gather rules in order.
        var rules = Stream
                .concat(globalRules(), contextualRules(query))
                .filter(rule -> rule.queryType().isInstance(query))
                .map(rule -> {
                    // This cast is safe due to the isInstance check.
                    @SuppressWarnings("unchecked")
                    var cast = (Rule<Q, V>) rule;
                    return cast;
                })
                .toList();
        for (var rule : rules) {
            if (rule.handles(query, value)) {
                value = rule.handle(query, value);
                if (query.shouldShortCircuit(value)) {
                    callback.accept(rule);
                    break;
                }
            }
        }
        return value;
    }

}
