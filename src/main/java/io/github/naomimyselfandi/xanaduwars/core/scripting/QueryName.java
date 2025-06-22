package io.github.naomimyselfandi.xanaduwars.core.scripting;

import com.fasterxml.jackson.annotation.JsonValue;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import io.github.naomimyselfandi.xanaduwars.util.ExcludeFromCoverageReport;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/// The name of a query. A query name is the unqualified name of a query class,
/// with any trailing "Query" or "Event" suffix removed.
public record QueryName(@JsonValue String queryName) implements Function<Rule, Stream<Script>> {

    // In the future, we'll add validation here to prevent typos.

    /// Get a query's name.
    public static QueryName of(Query<?> query) {
        return instances().get(stripSuffix(query.getClass().getSimpleName()));
    }

    @Override
    public Stream<Script> apply(Rule rule) {
        return Stream.ofNullable(rule.getHandlers().get(this));
    }

    @Override
    public String toString() {
        return queryName;
    }

    private static Map<String, QueryName> instances() {
        @ExcludeFromCoverageReport // This is fully covered, but the report incorrectly flags it
        class Holder {
            private static final Map<String, QueryName> INSTANCES;
            static {
                try (var scan = new ClassGraph().acceptPackages("io.github.naomimyselfandi.*").scan()) {
                    INSTANCES = scan
                            .getClassesImplementing(Query.class)
                            .stream()
                            .map(ClassInfo::getSimpleName)
                            .map(QueryName::stripSuffix)
                            .collect(Collectors.toUnmodifiableMap(Function.identity(), QueryName::new));
                }
            }
        }
        return Holder.INSTANCES;
    }

    private static String stripSuffix(String simpleName) {
        return simpleName.replaceFirst("Event$|Query$", "");
    }

}
