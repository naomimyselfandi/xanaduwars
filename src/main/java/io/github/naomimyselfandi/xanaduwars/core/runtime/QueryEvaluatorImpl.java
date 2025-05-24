package io.github.naomimyselfandi.xanaduwars.core.runtime;

import io.github.naomimyselfandi.xanaduwars.core.*;
import io.github.naomimyselfandi.xanaduwars.core.queries.SubjectQuery;
import io.github.naomimyselfandi.xanaduwars.core.queries.TargetQuery;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

@RequiredArgsConstructor
@EqualsAndHashCode(callSuper = false)
final class QueryEvaluatorImpl extends AbstractQueryEvaluator {

    private final Ruleset ruleset;

    @Override
    protected Stream<Rule<?, ?>> globalRules() {
        return ruleset.globalRules().stream();
    }

    @Override
    protected Stream<Rule<?, ?>> contextualRules(Query<?> query) {
        return Stream.concat(
                query instanceof SubjectQuery<?> q ? rules(q.subject(), Type::rules) : Stream.of(),
                query instanceof TargetQuery<?> q ? rules(q.target(), Type::targetRules) : Stream.of()
        );
    }

    private static Stream<Rule<?, ?>> rules(Object object, Function<Type, List<Rule<?, ?>>> getter) {
        return types(object).map(getter).flatMap(List::stream);
    }

    private static Stream<Type> types(Object object) {
        return object instanceof Element element ? switch (element) {
            case Tile tile -> Stream.concat(nodeAndOwnerTypes(tile), type(tile.unit().orElse(null)));
            case Unit unit -> Stream.concat(nodeAndOwnerTypes(unit), type(unit.tile().orElse(null)));
            case Player player -> playerAndSpellTypes(player);
            case Spell spell -> Stream.concat(Stream.of(spell.type()), type(spell.owner().orElse(null)));
        } : Stream.of();
    }

    private static Stream<Type> type(@Nullable Element element) {
        return element == null ? Stream.empty() : Stream.of(element.type());
    }

    private static Stream<Type> nodeAndOwnerTypes(Node node) {
        return Stream.concat(
                Stream.of(node.type()),
                node.owner().stream().flatMap(QueryEvaluatorImpl::playerAndSpellTypes)
        );
    }

    private static Stream<Type> playerAndSpellTypes(Player player) {
        return Stream.concat(Stream.of(player.type()), player.activeSpells().stream().map(Spell::type));
    }

}
